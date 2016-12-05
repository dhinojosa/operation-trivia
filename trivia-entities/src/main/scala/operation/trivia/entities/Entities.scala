package operation.trivia.entities

case class Round(winners:List[Player])

case class Player(name:String)

case class Question(item: String, possibleAnswers:List[String], actualAnswer: Int)

object Start

object Stop

object Reset

case class NewRound(x: Int)

object Game

case class Answer(round: Int, answer: Int, time: Long)

case class Signon(player:Player)

case class Signoff(player:Player)

object RequestQuestion

case class Winners(round:Int, winners:List[Answer])

object RequestWinners

object Tick
