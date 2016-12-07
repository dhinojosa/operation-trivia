package actors

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.event.Logging
import operation.trivia.entities.{Answer, Player, Register, Unregister}


object ScorekeeperWebSocketActor {
  def props(out: ActorRef) = Props(new ScorekeeperWebSocketActor(out))
}

class ScorekeeperWebSocketActor(out: ActorRef) extends Actor {
  val log = Logging(context.system, this)

  val actorSelection: ActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2555/user/scoreKeeperActor")

  def receive: PartialFunction[Any, Unit] = {
    case "register" =>
      log.debug("host web socket actor register invoked")
      actorSelection ! Register(self)
    case winners:(Int, List[(Player, (Int, Long))]) =>
      out ! s"Round ${winners._1}\n" + winners._2
        .zipWithIndex.map(f => (f._1, f._2 + 1)).map(f => s"${f._2}. ${f._1._1.name}")
    case s:String => actorSelection ! Answer(Player("Foo"), 4)
  }

  override def preStart(): Unit = {
    actorSelection ! Register(self)
  }

  override def postStop(): Unit = {
    actorSelection ! Unregister(self)
  }
}
