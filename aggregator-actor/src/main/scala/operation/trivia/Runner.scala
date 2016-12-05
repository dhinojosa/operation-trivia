package operation.trivia

import akka.actor.ActorSystem

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

/**
  * Created by danno on 12/5/16.
  */
object Runner extends App {
  val system: ActorSystem = ActorSystem("ActorSystem")
  implicit val executionContext = system.dispatcher


  println(s"Press RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  Await.ready(system.terminate(), 10 seconds)
}
