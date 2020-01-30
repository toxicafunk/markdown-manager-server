package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs._
import play.api.libs.ws._
import play.api.libs.json.{JsValue, _}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

import java.util.concurrent.CompletionStage

class ConfluenceController @Inject() (
    ws: WSClient,
    val controllerComponents: ControllerComponents
) extends BaseController {

    val baseurl = "https://confluence.eniro.com/rest/api"
    val user = "user"
    val pass = "password"

  def spaces() = Action { implicit request: Request[AnyContent] =>
    {
      val request: WSRequest = ws
        .url(s"$baseurl/space") //.withUrl()
          .withAuth(user, pass, WSAuthScheme.BASIC)
        .addHttpHeaders(
          "Content-Type" -> "application/json"
        )
        .addQueryStringParameters("type" -> "global")
      val futureResponse: Future[WSResponse] = request.get()
      println(futureResponse)
      val response: WSResponse = Await.result(futureResponse, 30.seconds)
      val json = response.json
      println(json)
      Ok(json)
    }
  }
}
