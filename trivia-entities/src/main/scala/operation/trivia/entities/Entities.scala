package operation.trivia.entities

import akka.actor.ActorRef

case class Round(winners:List[Player])

case class Player(name:String)

@SerialVersionUID(510L)
case class Question(item: String, possibleAnswers:List[String], actualAnswer: Int)  extends Serializable

object NewRound extends Serializable

@SerialVersionUID(401L)
object Game extends Serializable

case class Answer(player: Player, answer: Int)

case class SignOn(player:Player)

case class SignOff(player:Player)

@SerialVersionUID(222L)
object RequestQuestion extends Serializable

case class Winners(round:Int, winners:List[Answer])

@SerialVersionUID(505L)
object Tick extends Serializable

@SerialVersionUID(1940L)
case class Register(a:ActorRef) extends Serializable

@SerialVersionUID(1941L)
case class Unregister(a:ActorRef) extends Serializable

//New Events
@SerialVersionUID(1914L)
object GameStart extends Serializable

@SerialVersionUID(1915L)
object GameStop extends Serializable

//New Events
@SerialVersionUID(1916L)
case class QuestionStart(question: Question) extends Serializable

@SerialVersionUID(1917L)
case class QuestionStop(question: Question) extends Serializable

@SerialVersionUID(1918L)
case object RoundStart extends Serializable

@SerialVersionUID(1919L)
case class RoundNext(number:Int) extends Serializable

@SerialVersionUID(1920L)
case object RoundStop extends Serializable

@SerialVersionUID(1925L)
case class RoundResults(players:List[Player]) extends Serializable

@SerialVersionUID(1926L)
case class QuestionResults(players:List[Player]) extends Serializable