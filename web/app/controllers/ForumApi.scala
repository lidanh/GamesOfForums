package controllers

import com.gamesofforums.exceptions.{LoginException, InvalidDataException}
import com.gamesofforums.{MailService, SendGridService, InMemoryStorage, ForumService}
import com.gamesofforums.domain._
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

  /* Post /api/createSubForum */
  def createSubforum = Action { implicit request =>
    val data = createSubForumForm.bindFromRequest.get

    // Todo: how to pass user implicitly.
    implicit val adminUser = Some(User(
      generateId,
      firstName = "some admin",
      lastName = "some admin",
      mail = "mailmailmail@mail.com",
      password = "****",
      _role = ForumAdmin))

    service.createSubforum(
      name = data.name,
      moderators = Seq(data.moderators)
    ) match {
      case Return(subforum) => Ok(Json.obj("subforum_id" -> subforum.id))
      case Throw(e: InvalidDataException) => BadRequest(errorResult(e.getMessage, Some(e.invalidData.mkString(", "))))
      case Throw(e) => BadRequest(errorResult(e.getMessage))
    }
  }

  /* Post /api/publish */
  var subforumIdExtract =""
  var subjectdExtract = ""
  var contentExtract = ""

  def publishPost = Action { implicit request =>

    publishForm.bindFromRequest.fold(
    hasError =>{
      // Failure
      BadRequest("error")
    },
    userData => {
        subforumIdExtract = userData.subforumId
        subjectdExtract = userData.subject
        contentExtract = userData.content
    })

    /*val data = publishForm.bindFromRequest.get
      })*/

    val id : IdType =  subforumIdExtract


    implicit val adminUser = Some(User(
      generateId,
      firstName = "God",
      lastName = "God",
      mail = "God@mail.com",
      password = "****",
      _role = God))


    service.publishPost(
      subforumId = id,
      subject = subjectdExtract,
      content = contentExtract
    ) match {
      case Return(post) => Ok(Json.obj("post_id" -> post.id))
      case Throw(e: InvalidDataException) => BadRequest(errorResult(e.getMessage, Some(e.invalidData.mkString(", "))))
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

  /* Create subforum form */
  case class SubforumData(name: String, moderators: String)
  val createSubForumForm = Form(mapping(
    "name" -> text,
    "moderators" -> text
  )(SubforumData.apply)(SubforumData.unapply))

  /* Login form */
  case class LoginData(mail: String, password: String)
  val loginForm = Form(mapping(
    "mail" -> text,
    "password" -> text
  )(LoginData.apply)(LoginData.unapply))

  /* Publish form */
  case class PublishData(subforumId: String, subject: String, content: String)
  val publishForm = Form(mapping(
    "subforumId" -> text,
    "subject" -> text,
    "content" ->text
  )(PublishData.apply)(PublishData.unapply))

  def errorResult(exceptionMessage: String, content: Option[String] = None) = {
    val base = Json.obj(
      ERROR_KEY -> exceptionMessage
    )

    content.fold(base)(content => base ++ Json.obj("content" -> content))
  }
}