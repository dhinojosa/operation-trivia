package controllers

import javax.inject._

import actors.{HostWebSocketActor, ScorekeeperWebSocketActor, SignonWebSocketActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc._

class GameController @Inject()(implicit system: ActorSystem,
                               materializer: Materializer,
                               webJarAssets: WebJarAssets)
  extends Controller {


  def signonSocket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => SignonWebSocketActor.props(out))
  }

  def hostSocket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => HostWebSocketActor.props(out))
  }

  def scorekeeperSocket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => ScorekeeperWebSocketActor.props(out))
  }

  def index = Action { request =>
    request.session.get("name").map { name =>
      Ok(views.html.game("Let's play trivia!.", name, webJarAssets))
    }.getOrElse(Redirect(routes.LoginController.loginIntro()))
  }
}
