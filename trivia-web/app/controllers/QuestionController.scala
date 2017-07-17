package controllers

import javax.inject._

import akka.actor.{ActorSelection, ActorSystem}
import operation.trivia.entities.Question
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.Logger

class QuestionController @Inject()(val webJarAssets: WebJarAssets,
                                   val actorSystem: ActorSystem,
                                   val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  val questionForm = Form(
    mapping(
      "item" -> nonEmptyText,
      "possibleAnswers" -> list(text),
      "actualAnswer" -> number
    )(Question.apply)(Question.unapply)
  )

  val questionActor: ActorSelection = actorSystem.actorSelection(
    "akka.tcp://ActorSystem@127.0.0.1:2554/user/questionActor")

  def getQuestionForm = Action {implicit request =>
    Ok(views.html.question.question("Create Question", questionForm, webJarAssets))
  }

  def index = Action {implicit request =>
    Ok(views.html.question.index("Create Question", webJarAssets))
  }

  def processQuestion = Action {implicit request =>
    questionForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.question.question("Create Question", formWithErrors, webJarAssets))
      },
      question => {
        Logger.debug(s"Sending Question: ${question.toString}")
        questionActor ! question
        Logger.debug(s"Question Sent: ${question.toString}")
        Redirect(routes.QuestionController.index())
      }
    )
  }
}
