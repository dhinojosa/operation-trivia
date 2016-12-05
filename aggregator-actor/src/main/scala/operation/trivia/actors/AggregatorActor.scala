package operation.trivia.actors

import akka.actor.Actor
import operation.trivia.entities.{Player, Reset}

import scala.collection.mutable.ArrayBuffer

class AggregatorActor extends Actor {
  private var currentCandidates:ArrayBuffer[(Player, Int, Long)] = ArrayBuffer.empty

  override def receive: Receive = {
    case Reset => currentCandidates.clear()
    case List =>
  }
}
