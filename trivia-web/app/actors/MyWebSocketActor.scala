package actors

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.event.Logging

object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
  val log = Logging(context.system, this)
  val actorSelection: ActorSelection = context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2552/user/signonActor")

  def receive: PartialFunction[Any, Unit] = {
    case msg: String =>
      log.debug("Received Message")
      actorSelection ! "Dan"
      out ! ("I received your message: " + msg)
  }
}
