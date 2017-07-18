package actors

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.event.Logging
import operation.trivia.entities._
import play.api.Logger
import play.api.libs.json._

object ScorekeeperWebSocketActor {
  def props(out: ActorRef) = Props(new ScorekeeperWebSocketActor(out))
}

class ScorekeeperWebSocketActor(out: ActorRef) extends Actor {

  val actorSelection: ActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2555/user/scoreKeeperActor")

  def receive: PartialFunction[Any, Unit] = {
    case "init" =>
      Logger.info("ScoreKeeperWebSocketActor Received: init")
      actorSelection ! Register(self)
    case s: String =>
      //Answer arrived from the web
      Logger.info(s"ScoreKeeperWebSocketActor Received String: $s")
      val json: JsValue = Json.parse(s)
      actorSelection ! Answer(Player((json \ "name").as[String]), (json \ "answer").as[Int])
    case RoundResults(persons) =>
      Logger.info(s"Round Results: $persons")
      out ! Json.stringify(Json.obj("round-results" -> Json.toJson(persons.map(p => p.name))))
    case QuestionResults(persons) =>
      Logger.info(s"Question Results: $persons")
      out ! Json.stringify(Json.obj("question-results" -> Json.toJson(persons.map(p => p.name))))
    case o =>
      Logger.info(s"ScoreKeeperWebSocketActor Received Unknown: $o")
      unhandled(o)
  }

  override def postStop(): Unit = {
    Logger.info("Actor stopping")
    actorSelection ! Unregister(self)
  }
}
