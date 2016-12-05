package operation.trivia.applications

import akka.actor.{ActorSystem, Props}
import operation.trivia.actors.{HostActor, QuestionActor, ScorekeeperActor, SignonActor}
import operation.trivia.entities.{Player, Signon, Tick}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object GameScheduler extends App {
  val system: ActorSystem = ActorSystem("ActorSystem")
  implicit val executionContext = system.dispatcher

  private val signonActorRef = system.actorOf(Props[SignonActor], "signonActor")
  private val hostActorRef = system.actorOf(Props[HostActor], "hostActor")
  private val questionActorRef = system.actorOf(Props[QuestionActor], "questionActor")
  private val scoreKeeperActorRef = system.actorOf(Props[ScorekeeperActor], "scoreKeeperActor")

  implicit val ticker = system.scheduler.schedule(0 seconds, 1 seconds, hostActorRef, Tick)

  signonActorRef ! Signon(Player("foozgoo"))
  signonActorRef ! Signon(Player("lazerblog"))

  val answerMap = Map('A' -> 0, 'B' -> 1, 'C' -> 2, 'D' -> 3)
  val answer = StdIn.readChar()

  println(s"Press RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  Await.ready(system.terminate(), 10 seconds)
}
