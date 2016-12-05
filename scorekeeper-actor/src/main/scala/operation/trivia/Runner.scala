package operation.trivia

import akka.actor.{ActorSystem, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object Runner extends App {
  val system: ActorSystem = ActorSystem("ActorSystem")
  implicit val executionContext = system.dispatcher
  private val scoreKeeperActorRef = system.actorOf(Props[ScorekeeperActor], "scoreKeeperActor")

  println(s"Press RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  Await.ready(system.terminate(), 10 seconds)
}
