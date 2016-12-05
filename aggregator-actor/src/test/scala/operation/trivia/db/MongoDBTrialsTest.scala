package operation.trivia.db

import com.mongodb.client.model.Filters
import operation.trivia.entities.Question
import org.mongodb.scala.bson.{BsonArray, BsonString}
import org.mongodb.scala.model.Updates
import org.mongodb.scala.{Completed, Document, MongoClient, MongoDatabase}
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

/**
  * Created by danno on 12/3/16.
  */
class MongoDBTrialsTest extends FunSuite with Matchers with BeforeAndAfter {

  val mongoClient: MongoDatabase =
    MongoClient().getDatabase("operation_trivia")

  test("Test reading collections from Mongo DB") {
    mongoClient.listCollectionNames().subscribe((s: String) => println(s))
    Thread.sleep(4000)
  }


  test("Test reading collections from Mongo DB get limit of the first one") {

    def createQuestion(doc: Document): Option[Question] = {
      import collection.JavaConverters._
      for (
        item         <- doc.get("question").map(_.asString.getValue);
        actualAnswer <- doc.get("actualAnswer").map(_.asNumber().intValue());
        items        <- doc.get("answers").map(bson => bson.asArray()
                        .getValues.asScala.map(i => i.asString().getValue))
                        .map(_.toList)
      ) yield Question(item, items, actualAnswer)
    }

    mongoClient.getCollection("questions")
      .findOneAndUpdate(Filters.eq("taken", false), Updates.set("taken", true)).map(createQuestion)
      .subscribe((q: Option[Question]) => println(q))

    println("Done")
    Thread.sleep(4000)
  }

  test("Test create a Question") {
    def createDocument(question:Question): Document = {
      Document("question" -> question.item,
        "actualAnswer" -> question.actualAnswer,
        "answers" -> BsonArray(question.possibleAnswers.map(s => BsonString(s))))
    }

    mongoClient.getCollection("questions").insertOne(createDocument(Question("What is the capitol of Wyoming?",
                                                                    List("Cheyenne","Casper","Jackson","Sheridan"), 0)))
                                           .subscribe((c:Completed) => println(c))

    println("Done")
    Thread.sleep(4000)
  }
}
