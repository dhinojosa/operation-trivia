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
  private[SignonActor] val log = Logging(context.system, this)
  private[SignonActor] val hostActorSelection =
    context.system.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2553/user/hostActor")

  override def receive: Receive = {
    case SignOn(p:Player) =>
      log.debug("Signing on player: {}", p)
      players += p
      if (players.nonEmpty) {
        log.debug("Single player signed on, starting game")
        hostActorSelection ! GameStart
      }
    case SignOff(p:Player) =>
      //No more players, stop the game
      log.debug("Signing off player: {}", p)
      players -= p
      if (players.isEmpty) {
        log.debug("No players signed on, stopping game")
        hostActorSelection ! GameStop
      }
  }
}
