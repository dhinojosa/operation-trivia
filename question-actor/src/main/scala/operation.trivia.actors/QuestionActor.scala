package operation.trivia.actors

import akka.actor.Actor
import akka.event.Logging
import com.mongodb.client.model.Filters
import operation.trivia.entities.{Question, RequestQuestion}
import org.mongodb.scala.bson.{BsonArray, BsonString}
import org.mongodb.scala.model.Updates
import org.mongodb.scala.{Completed, Document, MongoClient}

/** Question Actor will:
  * * Get a request from the client for a new question
  * * Will be asked for three questions from the game
  * * Will need to ensure that the questions have not been asked.
  */

/**
  * ideas
  * reactive streaming
  * reactive actors
  *
  */
class QuestionActor extends Actor {
  val log = Logging(context.system, this)

  private def createQuestion(doc: Document): Option[Question] = {
    import collection.JavaConverters._
    for (
      item <- doc.get("question").map(_.asString.getValue);
      actualAnswer <- doc.get("actualAnswer").map(_.asNumber().intValue());
      items <- doc.get("answers").map(_.asArray()
        .getValues.asScala.map(_.asString().getValue))
        .map(_.toList)
    ) yield Question(item, items, actualAnswer)
  }

  private def createDocument(question: Question): Document = {
    Document("question" -> question.item,
      "actualAnswer" -> question.actualAnswer,
      "answers" -> BsonArray(question.possibleAnswers.map(s => BsonString(s))))
  }

  override def receive: Receive = {
    case q: Question =>
      MongoClient().getDatabase("operation_trivia").getCollection("questions")
        .insertOne(createDocument(q)).subscribe((c: Completed) => println(c))

    case RequestQuestion =>
      val keptSender = sender
      val collection = MongoClient().getDatabase("operation_trivia").getCollection("questions")
      //See if it is empty
      collection
        .count(Filters.eq("taken", false)).subscribe { (n: Long) =>
           if (n == 0) keptSender ! None
           else {
             collection.findOneAndUpdate(Filters.eq("taken", false), Updates.set("taken", true))
               .map(d => createQuestion(d)).subscribe((q:Option[Question]) => keptSender ! q)
           }
      }
  }
}
