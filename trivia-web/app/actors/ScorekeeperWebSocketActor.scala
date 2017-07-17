package actors

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.event.Logging
import operation.trivia.entities.{Answer, Player, Register, Unregister}
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
      Logger.info("Scorekeeper init")
      actorSelection ! Register(self)
    //    //    case winners: (Int, List[(Player, (Int, Long))]) =>
    //    //      out ! s"Round ${winners._1}\n" + winners._2
    //    //        .zipWithIndex.map(f => (f._1, f._2 + 1)).map(f => s"${f._2}. ${f._1._1.name}")
    case s: String =>
      Logger.info(s"Got answer from: $s")
      val json: JsValue = Json.parse(s)
      actorSelection ! Answer(Player((json \ "name").as[String]), (json \ "answer").as[Int])
    case o =>
      Logger.info(s"Got something else $o")
      unhandled(o)
  }

  override def postStop(): Unit = {
    //    actorSelection ! Unregister(self)
  }
}
