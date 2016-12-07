package operation.trivia.entities

import akka.actor.ActorRef

case class Round(winners:List[Player])

case class Player(name:String)

case class Question(item: String, possibleAnswers:List[String], actualAnswer: Int)

@SerialVersionUID(19L)
object Start extends Serializable

@SerialVersionUID(22L)
object Stop extends Serializable

@SerialVersionUID(90L)
object Reset extends Serializable

case class NewRound(x: Int)

@SerialVersionUID(401L)
object Game extends Serializable

case class Answer(player: Player, answer: Int)

case class Signon(player:Player)

case class Signoff(player:Player)

@SerialVersionUID(222L)
object RequestQuestion extends Serializable

case class Winners(round:Int, winners:List[Answer])

@SerialVersionUID(505L)
object Tick extends Serializable

case class Register(a:ActorRef)

case class Unregister(a:ActorRef)
