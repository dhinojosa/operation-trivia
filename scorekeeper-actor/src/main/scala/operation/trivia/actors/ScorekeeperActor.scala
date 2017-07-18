package operation.trivia.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import operation.trivia.entities._

import scala.collection.{immutable, mutable}

class ScorekeeperActor extends Actor {
  val log = Logging(context.system, this)

  val questionAnswers: mutable.Map[Player, (Int, Long)] = mutable.Map[Player, (Int, Long)]()
  val roundWinners: mutable.Map[Player, Long] = mutable.Map[Player, Long]()

  var currentRound: Option[Int] = None
  var correctAnswerOption: Option[Int] = None

  def sendToWebClients(a: Any): Unit = {
    webClients.foreach(ref => ref ! a)
  }

  var startTime = 0L

  private val webClients = mutable.Set[ActorRef]()

  override def receive: Receive = {
    case QuestionStart =>
      log.info("ScoreKeeperActor: QuestionStart Called")
      questionAnswers.clear()
      startTime = System.currentTimeMillis()
    case QuestionStop =>
      log.info("ScoreKeeperActor: QuestionStop Called")
      val winnerMap: Map[Player, (Int, Long)] = correctAnswerOption.map(answer =>
        questionAnswers.toMap
          .filter { case (_, (a, _)) => a == answer }).getOrElse(Map())

      log.info("W: winnerMap:{}", winnerMap)
      val winners = winnerMap
        .toList
        .sortBy { case (_, (_, t)) => t }
        .map(t => t._1)

      roundWinners ++= winnerMap
        .groupBy(t => t._1)
        .map { case (name, map) => (name, map.values.foldLeft(0L)(_ + _._2))}

      log.info("X: question winners:{}", winners)
      log.info("Y: round winners so far:{}", roundWinners)

      sendToWebClients(QuestionResults(winners))
    case RoundStart =>
      log.info("ScoreKeeperActor: RoundStart Called")
      roundWinners.clear()
    case RoundStop =>
      log.info("ScoreKeeperActor: RoundStop Called")
      val winners: List[Player] =
        roundWinners.toList.sortBy(t => t._2).map(t => t._1)
      sendToWebClients(RoundResults(winners))
    case GameStart =>
      log.info("ScoreKeeperActor: GameStart Called")
    case GameStop =>
      log.info("ScoreKeeperActor: GameStop Called")
    case Question(_, _, actualAnswer) =>
      log.info("ScoreKeeperActor: Question submitted with the correct answer: {}", actualAnswer)
      correctAnswerOption = Some(actualAnswer)
    case Answer(player, answer) =>
      log.info("ScoreKeeperActor: Answer for player: {} with answer: {}", player, answer)
      questionAnswers += (player -> (answer, System.currentTimeMillis() - startTime))
    case Register(ar) =>
      log.info("ScoreKeeperActor: Register Called")
      webClients += ar
    case Unregister(ar) =>
      log.info("ScoreKeeperActor: Unregister Called")
      webClients -= ar
  }
}
