package actors

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import operation.trivia.entities.{Question, Register, Unregister}
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
      Logger.debug("Init connection")
      actorSelection ! Register(self)
    case q: Question =>
      Logger.debug("question delivered")
      val json: JsValue = Json.toJson(Json.obj("item" -> q.item, "possibleAnswers" ->
        q.possibleAnswers, "actualAnswer" -> q.actualAnswer))
      out ! Json.stringify(json)
    case y: Int =>
      Logger.debug("Got a message: {}" + y)
      out ! Json.stringify(Json.obj("secondsLeft" -> y))
    case m =>
      Logger.debug("Didn't find anything for" + m)
  }

  override def postStop(): Unit = {
    actorSelection ! Unregister(self)
  }
}