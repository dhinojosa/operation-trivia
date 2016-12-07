package actors

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.event.Logging

object SignonWebSocketActor {
  def props(out: ActorRef) = Props(new ScorekeeperWebSocketActor(out))
}

class SignonWebSocketActor(out: ActorRef) extends Actor {
  val log = Logging(context.system, this)
  val actorSelection: ActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2556/user/signonActor")

  def receive: PartialFunction[Any, Unit] = {
    case content: String => println("Got content" + content)
  }
}