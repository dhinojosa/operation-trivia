package operation.trivia.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import operation.trivia.entities._

import scala.collection.mutable

/**
  * The Conductor Actor is the host of the game
  * It will start and stop the game on the web
  * It will receive a trigger saying it is either a new round or new game
  */
class HostActor extends Actor {

  val log = Logging(context.system, this)
  private var round = 1
  private val questionActorSelection = context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2554/user/questionActor")
  private val scoreKeeperActorSelection = context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2555/user/scoreKeeperActor")
  private val webClients = mutable.Set[ActorRef]()
  private var tickListen = false
  private var seconds = 0
  private var currentQuestion:Option[Question] = None

  def sendToWebClients(a:Any): Unit = {
     log.debug("Number of clients listening:" + webClients.size)
     webClients.foreach(ref => ref ! a)
  }

  override def receive: Receive = {
    case Start =>
      log.debug("Start invoked")
      questionActorSelection ! RequestQuestion
    case Some(q:Question) =>
      log.debug("Question invoked {}", q)
      currentQuestion = Some(q)
      tickListen = true
    case Register(ar) =>
      log.debug("Registered Web Client")
      webClients += ar
    case Unregister(ar) =>
      log.debug("Unregistered Web Client")
      webClients -= ar
    case None =>
      log.debug("No Questions invoked")
      currentQuestion = None
      println("No questions in the database, please fill")
    case Stop  => println("Stop") //Stop Mean Stop Game
      log.debug("Stop invoked")
      tickListen = false
      seconds = 0
    case Tick =>
      if (tickListen) {
        log.debug("Game in session!")
        sendToWebClients(30 - seconds)
        seconds += 1
        if (seconds == 30) {  //Game is 30 seconds long
          tickListen = false
          currentQuestion = None
          scoreKeeperActorSelection ! Stop //Times up!
        }
      }
    case x =>
      log.error("Received ill conceived message: {}", x)
      unhandled(x)
  }
}