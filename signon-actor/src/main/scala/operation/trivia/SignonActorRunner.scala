package operation.trivia

import akka.actor.{ActorSystem, Props}
import operation.trivia.actors.SignonActor

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

object SignonActorRunner extends App {
  val system: ActorSystem = ActorSystem("ActorSystem")
  system.actorOf(Props[SignonActor], "signonActor")

  println(s"Press RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  Await.ready(system.terminate(), 10 seconds)
}
