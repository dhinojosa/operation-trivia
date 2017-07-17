package operation.trivia.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import operation.trivia.entities._

import scala.collection.mutable

class HostActor extends Actor {

  val log = Logging(context.system, this)
  private var round = 1
  private val questionActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2554/user/questionActor")
  private val scoreKeeperActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2555/user/scoreKeeperActor")
  private val webClients = mutable.Set[ActorRef]()
  private var tickListen = false
  private var waitingForQuestion = false
  private var seconds = 0
  private var currentQuestion:Option[Question] = None

  def infoWebClients(a:Any): Unit = {
     log.info("Sending to {} web clients message: {}", webClients.size, a)
     webClients.foreach(ref => ref ! a)
  }

  override def receive: Receive = {
    case Start =>
      log.info("Start invoked due to an interest")
      questionActorSelection ! RequestQuestion
    case Some(q:Question) =>
      log.info("Question received {}", q)
      waitingForQuestion = false
      currentQuestion = Some(q)
      infoWebClients(q)
      tickListen = true
      seconds = 0
    case Register(ar) =>
      log.info("Registered Web Client")
      webClients += ar
      if (webClients.size == 1) self ! Start
    case Unregister(ar) =>
      log.info("Unregistered Web Client")
      webClients -= ar
      if (webClients.isEmpty) self ! Stop
    case None =>
      log.info("No Questions invoked")
      currentQuestion = None
      waitingForQuestion = true
      infoWebClients(None)
    case Stop  =>
      log.info("Stop invoked")
      tickListen = false
      seconds = 0
    case Tick =>
      if (tickListen) {
        log.info("Game in session with {} clients", webClients.size)
        infoWebClients(30 - seconds)
        seconds += 1
        if (seconds > 30) {
          tickListen = false
          currentQuestion = None
          scoreKeeperActorSelection ! Stop //Times up!
          questionActorSelection ! RequestQuestion // Next question!
          seconds = 0
        }
      } else if (waitingForQuestion) {
        questionActorSelection ! RequestQuestion
        log.info("Waiting for question with {} clients", webClients.size)
      } else {
        log.info("Game is not a good state with {} clients", webClients.size)
        log.info(currentQuestion.toString)
        log.info(tickListen.toString)
        log.info(seconds.toString)
        log.info(waitingForQuestion.toString)
        log.info(webClients.toString)
      }
    case x =>
      log.error("Received ill conceived message: {}", x)
      unhandled(x)
  }
}