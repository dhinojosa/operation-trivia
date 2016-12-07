package operation.trivia.actors

import akka.actor.Actor
import operation.trivia.entities._

/**
  * Created by danno on 12/1/16.
  */
class AggregatorRouteeActor extends Actor {
  override def receive: Receive =  {
    case s:String => println(s)
  }
}
