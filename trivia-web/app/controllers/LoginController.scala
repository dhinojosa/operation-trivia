package controllers

import javax.inject._

import akka.actor.{ActorSelection, ActorSystem}
import operation.trivia.entities.{Player, Signoff, Signon}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

class LoginController @Inject()(val webJarAssets: WebJarAssets,
                                val actorSystem: ActorSystem,
                                val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  val loginForm = Form(
    mapping(
      "name" -> nonEmptyText
    )(Player.apply)(Player.unapply)
  )

  val signonActor: ActorSelection = actorSystem.actorSelection(
    "akka.tcp://ActorSystem@127.0.0.1:2556/user/signonActor")

  def loginIntro = Action {
    Ok(views.html.login(loginForm, webJarAssets))
  }

  def login = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.login(formWithErrors, webJarAssets))
      },
      player => {
        signonActor ! Signon(player)
        Redirect(routes.GameController.index()).withSession("name" -> player.name)
      }
    )
  }

  def logout = Action { request =>
    request.session.get("name").foreach { name =>
      signonActor ! Signoff(Player(name))
    }
    Redirect(routes.LoginController.loginIntro()).withNewSession
  }
}
