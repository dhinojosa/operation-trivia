package operation.trivia.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import operation.trivia.entities._

import scala.collection.mutable

class HostActor extends Actor {

  val log = Logging(context.system, this)

  private val questionActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2554/user/questionActor")
  private val scoreKeeperActorSelection =
    context.actorSelection("akka.tcp://ActorSystem@127.0.0.1:2555/user/scoreKeeperActor")

  //All the web clients
  private val webClients = mutable.Set[ActorRef]()

  //State
  private var gameInProgress = false
  private var questionInProgress = false
  private var waitingForQuestion = false
  private var onBreak = false

  //Timers
  private var gameSeconds = 0
  private var breakSeconds = 0

  //Tracker
  private var currentRound: Option[Int] = None
  private var currentQuestion: Option[Question] = None
  private var questionsAsked: Option[Int] = None

  //Send info to website
  def infoWebClients(a: Any): Unit = {
    log.info("Sending to {} web clients message: {}", webClients.size, a)
    webClients.foreach(ref => ref ! a)
  }

  override def receive: Receive = {
    case GameStart =>
      log.info("Start invoked due to more than one player")
      infoWebClients(GameStart)
      scoreKeeperActorSelection ! GameStart
      self ! RoundStart
      gameInProgress = true
    case GameStop =>
      log.info("Stop invoked")
      gameSeconds = 0
      questionInProgress = false
      gameInProgress = false
      waitingForQuestion = false
      currentRound = None
      currentQuestion = None
      questionsAsked = None
      scoreKeeperActorSelection ! GameStop
    case RoundStart =>
      infoWebClients(RoundStart)
      scoreKeeperActorSelection ! RoundStart
      if (currentRound.isEmpty) currentRound = Some(1) else currentRound= currentRound.map(_ + 1)
      scoreKeeperActorSelection ! RoundNext(currentRound.get)
      onBreak = false
      breakSeconds = 0
      questionsAsked = None
      self ! QuestionStart
    case RoundStop =>
      infoWebClients(RoundStop)
      onBreak = true
      scoreKeeperActorSelection ! RoundStop
    case Some(q: Question) =>
      log.info("Question received {}", q)
      waitingForQuestion = false
      currentQuestion = Some(q)
      scoreKeeperActorSelection ! QuestionStart
      scoreKeeperActorSelection ! q
      if (questionsAsked.isEmpty) {
        questionsAsked = Some(1)
      } else questionsAsked = questionsAsked.map(x => x + 1)
      infoWebClients(q)
      questionInProgress = true
      gameSeconds = 0
    case None =>
      log.info("No Questions invoked")
      currentQuestion = None
      waitingForQuestion = true
      infoWebClients(None)
    case Register(ar) =>
      log.info("Registered Web Client")
      webClients += ar
      if (webClients.size == 1 && !gameInProgress) self ! GameStart
    case Unregister(ar) =>
      log.info("Unregistered Web Client")
      webClients -= ar
      if (webClients.isEmpty) {
        self ! GameStop
      }
    case QuestionStart =>
      questionActorSelection ! RequestQuestion
    case QuestionStop =>
      questionInProgress = false
      currentQuestion = None
      scoreKeeperActorSelection ! QuestionStop //Times up!
      gameSeconds = 0
      if (questionsAsked.getOrElse(0) == 4) {
        log.info("Question Stop: Stopping Round")
        self ! RoundStop
      } else {
        log.info("Question Stop: Requesting Question")
        questionActorSelection ! RequestQuestion // Next question!
      }
    case Tick =>
      if (questionInProgress) {
        log.info("Game in session with {} clients", webClients.size)
        infoWebClients(30 - gameSeconds)
        gameSeconds += 1
        if (gameSeconds > 30) self ! QuestionStop
      } else if (waitingForQuestion) {
        questionActorSelection ! RequestQuestion
        log.info("Waiting for question with {} clients", webClients.size)
      } else if (onBreak) {
        breakSeconds += 1
        log.info("On break for {} seconds", breakSeconds)
        if (breakSeconds > 5 * 60) self ! RoundStart
      } else {
        log.info("Game is not running; there are {} clients", webClients.size)
        //Turn on to find out more
        //        log.info(currentQuestion.toString)
        //        log.info(questionInProgress.toString)
        //        log.info(gameSeconds.toString)
        //        log.info(waitingForQuestion.toString)
        //        log.info(webClients.toString)
      }
    case x =>
      log.error("Received ill conceived message: {}", x)
      unhandled(x)
  }
}