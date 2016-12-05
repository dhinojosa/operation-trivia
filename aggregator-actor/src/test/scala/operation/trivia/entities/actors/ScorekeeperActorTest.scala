package operation.trivia.entities.actors

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import operation.trivia.actors.ScorekeeperActor
import operation.trivia.entities.{Reset, Start}
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

class ScorekeeperActorTest extends FunSuite with Matchers with BeforeAndAfter {
  implicit val actorSystem = ActorSystem("SampleActorSystem")
  val actorRef = TestActorRef[ScorekeeperActor]
  val underlyingActor: ScorekeeperActor = actorRef.underlyingActor


  test("A Game Actor should reset the game whenever required") {

  }

  test("A Game that starts should have a round already in play") {

  }
}
