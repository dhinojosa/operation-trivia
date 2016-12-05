package operation.trivia.entities.actors

import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}
import akka.testkit.TestActorRef
import operation.trivia.entities.{Reset, Start}

class GameActorTest extends FunSuite with Matchers with BeforeAndAfter {
//  implicit val actorSystem = ActorSystem("SampleTest")
//  val actorRef = TestActorRef[GameActor]
//  val underlyingActor: GameActor = actorRef.underlyingActor
//
//  test("A Game Actor should reset the game whenever required") {
//    actorRef ! Reset
//    underlyingActor.currentRound should be ('empty)
//  }
//
//  test("A Game that starts should have a round already in play") {
//     actorRef ! Start
//     underlyingActor.currentRound should not be None
//  }
}
