package operation.trivia.actors

import akka.actor.Actor
import operation.trivia.entities._

/**
  * Created by danno on 12/1/16.
  */
class AggregatorRouteeActor extends Actor {
  override def receive: Receive =  {
    case Answer(round, answer, time) => //Retrieve Answer
    case Start => //Start Aggregator to receive.
    case Stop =>  //Accept No More Answers
    case RequestWinners => //Send Winners to The Aggregator
    case Reset => //Clear the table of winners
  }
}
