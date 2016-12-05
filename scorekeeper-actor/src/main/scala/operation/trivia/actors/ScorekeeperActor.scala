package operation.trivia.actors

import akka.actor.Actor
import akka.event.Logging
import operation.trivia.entities._


/** Scorekeeper Actor will:
  * * Will receive from the aggregator actor the latest answers
  * * Will be asked for three questions from the game
  * * Will need to ensure that the questions have not been asked.
  * * Will need to get the answer from
  */
class ScorekeeperActor extends Actor {
  val log = Logging(context.system, this)
  var currentRound = 1

  override def receive:Receive = {
    case Start =>
      log.debug("Received Start of Round")
    case Stop => log.debug("Received Stop of Round")
  }
}
