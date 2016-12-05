package operation.trivia.actors

import akka.actor.{Actor, ActorSelection}
import akka.event.Logging
import operation.trivia.entities._

import scala.collection.mutable.ArrayBuffer

/**
  * Sign on Actor is used to determine the number of players in the game
  * If the Scorekeeper notices that there are only a certain number of players playing then
  * we should stop.
  * Signon() is when someone signs on, and called from client
  * Signoff() is called by the scorekeeper
  */
class SignonActor extends Actor {

  private[SignonActor] val players: ArrayBuffer[Player] = ArrayBuffer[Player]()
  private[SignonActor] val hostActorRef: ActorSelection = context.actorSelection("../hostActor")
  private[SignonActor] val log = Logging(context.system, this)

  override def receive: Receive = {
    case s:String =>
      log.info("Got message {}:", s)
      sender ! "Got it, we cool!"
    case Signon(p:Player) =>
      log.debug("Signing on player: {}", p)
      players += p
      if (players.size > 1) {
        log.debug("More than 1 player signed on, starting game")
        hostActorRef ! Start
      }
    case Signoff(p:Player) =>
      //No more players, stop the game
      log.debug("Signing off player: {}", p)
      if (players.size <= 1) {
        log.debug("Less than 1 player signed on, stopping game")
        hostActorRef ! Stop
      }
      players -= p
  }
}
