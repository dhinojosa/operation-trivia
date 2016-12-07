package operation.trivia

import akka.actor.{ActorSystem, Props}
import operation.trivia.actors.ScorekeeperActor

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

object ScoreKeeperActorRunner extends App {
  val system: ActorSystem = ActorSystem("ActorSystem")
  implicit val executionContext = system.dispatcher
  private val scoreKeeperActorRef = system.actorOf(Props[ScorekeeperActor], "scoreKeeperActor")

  println(s"Press RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  Await.ready(system.terminate(), 10 seconds)
}
