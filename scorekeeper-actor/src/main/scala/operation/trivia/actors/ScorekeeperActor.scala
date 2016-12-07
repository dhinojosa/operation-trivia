package operation.trivia.actors

import akka.actor.{Actor, ActorRef, ActorSelection}
import akka.event.Logging
import operation.trivia.entities._

import scala.collection.immutable.Seq
import scala.collection.mutable

class ScorekeeperActor extends Actor {
  val log = Logging(context.system, this)
  var currentRound = 1
  val answers: mutable.Map[Player, (Int, Long)] = mutable.Map[Player, (Int, Long)]()

  var correctAnswer = -1

  def sendToWebClients(a:Any): Unit = {
    webClients.foreach(ref => ref ! a)
  }

  private val webClients = mutable.Set[ActorRef]()

  override def receive:Receive = {
    case Reset =>
      log.debug("Reset Called")
      answers.clear()
      currentRound = 1
    case Stop =>
      log.debug("Stop Called")
      val winners: List[(Player, (Int, Long))] =
        answers.toMap.filter((x: (Player, (Int, Long))) => x._2._1 == correctAnswer)
        .toList.sortBy((x: (Player, (Int, Long))) => x._2._2)
      sendToWebClients(currentRound -> winners)
    case Question(_, _, actualAnswer) =>
      //We receive the question from the host and all we need is the
      //correct answer
      log.debug("Question Called")
      correctAnswer = actualAnswer
    case Register(ar) =>
      log.debug("Register Called")
      webClients += ar
    case Unregister(ar) =>
      log.debug("Unregister Called")
      webClients -= ar
    case Answer(player, answer) =>
      log.debug("Received Answer")
      answers += (player -> (answer, System.currentTimeMillis()))


  }
}
