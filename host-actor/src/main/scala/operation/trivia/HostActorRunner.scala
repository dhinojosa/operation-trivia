package operation.trivia

import akka.actor.{ActorSystem, Props}
import operation.trivia.actors.HostActor
import operation.trivia.entities.Tick

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

/**
  * Created by danno on 12/5/16.
  */
object HostActorRunner extends App {
  val system: ActorSystem = ActorSystem("ActorSystem")
  implicit val executionContext = system.dispatcher
  private val hostActorRef = system.actorOf(Props[HostActor], "hostActor")

  val cancellable =
    system.scheduler.schedule(
      0 seconds,
      1 seconds,
      hostActorRef,
      Tick)

  println(s"Press RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

  cancellable.cancel()
  Await.ready(system.terminate(), 10 seconds)
}
