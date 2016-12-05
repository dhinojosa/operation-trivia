package operation.trivia.actors

import akka.actor.Actor
import akka.event.Logging
import operation.trivia.entities._

/**
  * The Conductor Actor is the host of the game
  * It will start and stop the game on the web
  * It will receive a trigger saying it is either a new round or new game
  */
class HostActor extends Actor {

  val log = Logging(context.system, this)
  private var round = 1
  private val questionActorSelection = context.actorSelection("../questionActor")
  private val scoreKeeperActorSelection = context.actorSelection("../scoreKeeperActor")
  private var tickListen = false
  private var seconds = 0

  override def receive: Receive = {
    case Start =>
      println("Start: Let's begin our game! In one minute")
      questionActorSelection ! RequestQuestion
    case Some(q:Question) =>
      println("The question is...")
      println(q.item)
      println()
      println("Is it...")
      println("A." + q.possibleAnswers(0))
      println("B." + q.possibleAnswers(1))
      println("C." + q.possibleAnswers(2))
      println("D." + q.possibleAnswers(3))
      println("You have 1 minute to answer")
      tickListen = true
      seconds = 0
    case None =>
      println("No questions in the database, please fill")
    case Stop  => println("Stop")
      round = 1
    case Tick =>
      log.debug("Received Tick!")
      if (tickListen) {
        seconds += 1
        log.debug("Game in session!")
        if (seconds > 30) {  //Game is 30 seconds long
          tickListen = false
          scoreKeeperActorSelection ! Stop //Times up!
        }
      }
    case x =>
      log.error("Received ill conceived message: {}", x)
      unhandled(x)
  }
}
