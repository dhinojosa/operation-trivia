package actors

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import operation.trivia.entities._
import play.api.Logger
import play.api.libs.json._

object HostWebSocketActor {
  def props(out: ActorRef) = Props(new HostWebSocketActor(out))
}

class HostWebSocketActor(out: ActorRef) extends Actor {
  val actorSelection: ActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2553/user/hostActor")

  def receive: PartialFunction[Any, Unit] = {
    case "init" =>
      Logger.debug("Init connection Host")
      actorSelection ! Register(self)
    case q: Question =>
      Logger.debug("Question Delivered to Web!")
      val json: JsValue = Json.toJson(
        Json.obj("question" -> Json.obj("item" -> q.item, "possibleAnswers" ->
          q.possibleAnswers, "actualAnswer" -> q.actualAnswer))
      )
      out ! Json.stringify(json)
    case y: Int =>
      Logger.debug("Got an int message:" + y)
      out ! Json.stringify(Json.obj("secondsLeft" -> y))
    case None =>
      Logger.debug("There are no questions")
      out ! Json.stringify(Json.obj("error" -> "no questions"))
    case RoundNext(n) =>
      Logger.debug("Next Round:" + n)
      out ! Json.stringify(Json.obj("round" -> Json.obj("status" -> "next", "number" -> n)))
    case RoundStop =>
      Logger.debug("Round Stop:")
      out ! Json.stringify(Json.obj("round" -> Json.obj("status" -> "stop")))
    case RoundStart =>
      Logger.debug("Round Start:")
      out ! Json.stringify(Json.obj("round" -> Json.obj("status" -> "start")))
    case m =>
      Logger.debug("Didn't find anything for" + m)
  }

  override def postStop(): Unit = {
    actorSelection ! Unregister(self)
  }
}