package controllers

import javax.inject._

import actors.MyWebSocketActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.api._

class GameController @Inject() (implicit system: ActorSystem,
                                         materializer: Materializer,
                                         webJarAssets:WebJarAssets)
                                 extends Controller {
  def socket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(out))
  }

  def index = Action {
    Ok(views.html.game("Your new application is ready.", webJarAssets))
  }
}
