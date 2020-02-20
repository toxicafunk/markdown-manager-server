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

  implicit val ec = ExecutionContext.global

  val postBody = (title: String, body: String) => Json.obj(
    "type"  -> "page",
    "title" -> title,
    "space" -> Json.obj{"key" -> "DOC"},
    "body"  -> Json.obj(
      "storage" -> Json.obj(
        "value" -> body,
        "representation" -> "storage"
      )
    )
  )

  def spaces() = Action.async { implicit request: Request[AnyContent] =>
    {
      val request: WSRequest = ws
        .url(s"$baseurl/content/36247644") //.withUrl()
        .withAuth(user, pass, WSAuthScheme.BASIC)
        .addHttpHeaders(
          "Content-Type" -> "application/json"
        )
        .addQueryStringParameters(
          "type" -> "page",
          "spaceKey" -> "DOC",
          "expand" -> "body.storage"
        )
      val futureResponse: Future[WSResponse] = request.get()
      println(futureResponse)
      //val response: WSResponse = Await.result(futureResponse, 30.seconds)
      futureResponse.map(r => {
        val json = r.json
        println(json)
        Ok(json)
      })
    }
  }

  def create(title: String) = Action.async { implicit request: Request[AnyContent] =>
    {
      val requestConfluence: WSRequest = ws
        .url(s"$baseurl/content")
        .withAuth(user, pass, WSAuthScheme.BASIC)
        .addHttpHeaders(
          "Content-Type" -> "application/json"
        )

      val body = request.body.asText.getOrElse("No content was provided")
      val data = postBody(title, body)

      val futureResponse: Future[WSResponse] = requestConfluence.post(data)
      println(futureResponse)
      futureResponse.map(r => {
        val json = r.json
        println(json)
        Ok(json)
      })
    }
  }
}
