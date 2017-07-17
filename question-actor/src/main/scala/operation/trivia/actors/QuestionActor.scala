package operation.trivia.actors

import akka.actor.Actor
import akka.event.Logging
import com.mongodb.client.model.Filters
import operation.trivia.entities.{Question, RequestQuestion}
import org.mongodb.scala.bson.{BsonArray, BsonString}
import org.mongodb.scala.connection.ConnectionPoolSettings
import org.mongodb.scala.model.Updates
import org.mongodb.scala.{Completed, Document, MongoClient, MongoClientSettings}

/** Question Actor will:
  * * Get a request from the client for a new question
  * * Will be asked for three questions from the game
  * * Will need to ensure that the questions have not been asked.
  */
class QuestionActor extends Actor {
  val log = Logging(context.system, this)
  val connectionPoolSettings: ConnectionPoolSettings = ConnectionPoolSettings.builder().maxSize(100).build()
  val mongoClientSettings: MongoClientSettings = MongoClientSettings.builder().connectionPoolSettings(connectionPoolSettings).build()

  var client = MongoClient()

  override def preStart: Unit = {
    client = MongoClient()
  }

  override def postStop {
    client.close()
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    client.close()
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) {
    client = MongoClient()
    super.postRestart(reason)
  }

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
      "answers" -> BsonArray(question.possibleAnswers.map(s => BsonString(s))),
      "taken" -> false)
  }

  override def receive: Receive = {
    case q: Question =>
      log.debug("Creating Question: {}", q)
      client.getDatabase("operation_trivia").getCollection("questions")
        .insertOne(createDocument(q)).subscribe((c: Completed) => println(c))

    case RequestQuestion =>
      log.debug("Requesting Question")
      val keptSender = sender

      val collection = client.getDatabase("operation_trivia").getCollection("questions")
      //See if it is empty
      collection
        .count(Filters.eq("taken", false)).subscribe { (n: Long) =>
        if (n == 0) keptSender ! None
        else {
          collection
            .findOneAndUpdate(Filters.eq("taken", false), Updates.set("taken", true))
            .map(d => createQuestion(d))
            .subscribe((q: Option[Question]) => keptSender ! q)
        }
      }
  }
}
