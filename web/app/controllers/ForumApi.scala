package controllers

import com.gamesofforums.exceptions.{LoginException, InvalidDataException}
import com.gamesofforums.{MailService, SendGridService, InMemoryStorage, ForumService}
import com.gamesofforums.domain.{ForumPolicy, Forum}
import com.twitter.util.{Throw, Return}
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._


object ForumAPI extends Controller {
  import APIHelpers._

  val service = new ForumService(
    forum = new Forum(new ForumPolicy()),
    db = new InMemoryStorage,
    mailService = new MailService {
      override def sendMail(subject: String, recipients: Seq[String], content: String): Unit = {
        // TODO: Replace with real mail service
      }
    }
  )

  /* POST /api/register */
  def register = Action { implicit request =>
    val data = registrationForm.bindFromRequest.get

    service.register(
      firstName = data.firstname,
      lastName = data.lastname,
      mail = data.mail,
      password = data.password
    ) match {
      case Return(user) => Ok(Json.obj("user_id" -> user.id))
      case Throw(e: InvalidDataException) => BadRequest(errorResult(e.getMessage, Some(e.invalidData.mkString(", "))))
      case Throw(e) => BadRequest(errorResult(e.getMessage))
    }
  }

  /* Post /api/login */
  def login = Action { implicit request =>
    val data = loginForm.bindFromRequest.get

    service.login(
      mail = data.mail,
      password = data.password
    ) match {
      case Return(user) => Ok(Json.obj("user_id" -> user.id))
      case Throw(e: LoginException) => BadRequest(errorResult(e.getMessage, Some(e.message.mkString(", "))))
      case Throw(e) => BadRequest(errorResult(e.getMessage))
    }
  }
}

object APIHelpers {
  val ERROR_KEY = "error"

  /* Registration Form */
  case class RegistrationData(firstname: String, lastname: String, mail: String, password: String)
  val registrationForm = Form(mapping(
    "firstname" -> text,
    "lastname" -> text,
    "mail" -> text,
    "password" -> text
  )(RegistrationData.apply)(RegistrationData.unapply))

  /* Login form */
  case class LoginData(mail: String, password: String)
  val loginForm = Form(mapping(
    "mail" -> text,
    "password" -> text
  )(LoginData.apply)(LoginData.unapply))




  def errorResult(exceptionMessage: String, content: Option[String] = None) = {
    val base = Json.obj(
      ERROR_KEY -> exceptionMessage
    )

    content.fold(base)(content => base ++ Json.obj("content" -> content))
  }
}