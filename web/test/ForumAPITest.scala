import com.gamesofforums.domain._
import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import org.specs2.matcher.JsonMatchers

class ForumAPITest extends Specification with JsonMatchers {

  val baseEndpoint = "/api"

  sequential

  "User registeration" should {
    val registerEndpoint = s"$baseEndpoint/register"

    "successfully register the user upon valid details" in new WithApplication {
      val validDetails = Seq(
        "firstname" -> "kuki",
        "lastname" -> "buki",
        "mail" -> "some@mail.com",
        "password" -> "p@ssw0rd"
      )
      val response = route(FakeRequest(POST, registerEndpoint).withFormUrlEncodedBody(validDetails: _*)).get

      status(response) must be_==(OK)
      contentAsString(response) must /("user_id" -> ".+".r)
    }

    "return error upon invalid request" in new WithApplication() {
      val invalidDetails = Seq(
        "firstname" -> "kuki",
        "lastname" -> "buki",
        "mail" -> "invalid mail",
        "password" -> "p@ssw0rd"
      )
      val response = route(FakeRequest(POST, registerEndpoint).withFormUrlEncodedBody(invalidDetails: _*)).get

      status(response) must be_==(BAD_REQUEST)
      contentAsString(response) must /("error" -> "Invalid data")
    }

    "be able to login after successfully registered" in new WithApplication{
      val loginEndpoint = s"$baseEndpoint/login"
      val validDetails = Seq(
        "firstname" -> "kuki1",
        "lastname" -> "buki1",
        "mail" -> "mail@mail.com",
        "password" -> "p@ssw0rd"
      )

      val validLogin = Seq(
        "mail" -> "mail@mail.com",
        "password" -> "p@ssw0rd"
        )
      route(FakeRequest(POST, registerEndpoint).withFormUrlEncodedBody(validDetails: _*))
      val response = route(FakeRequest(POST, loginEndpoint).withFormUrlEncodedBody(validLogin:_*)).get

      status(response) must be_==(OK)
      contentAsString(response) must /("user_id" -> ".+".r)
    }
  }

  "User login" should {

    val loginEndpoint = s"$baseEndpoint/login"

    "fail if invalid details" in new WithApplication() {
      val InvalidDetails = Seq(
        "mail" -> "invalid@mail.com",
        "password" -> "1"
      )

      val response = route(FakeRequest(POST, loginEndpoint).withFormUrlEncodedBody(InvalidDetails:_*)).get
      status(response) must be_==(BAD_REQUEST)
      contentAsString(response) must /("error" ->"User is not registered")

    }
  }

  "Create Subforum" should {

    val createSubForumEndpoint = s"$baseEndpoint/createSubforum"
    val registerEndpoint = s"$baseEndpoint/register"

    "success if valid details" in new WithApplication() {


      /*val validModeratorDetails = Seq(
        "firstname" -> "first",
        "lastname" -> "name",
        "mail" -> "moderator@mailmail.com",
        "password" -> "p@ssw0rd"
      )*/

      val validModerators2Details = Seq(
        "firstname" -> "first",
        "lastname" -> "name",
        "mail" -> "admin@mailmail.com",
        "password" -> "p@ssw0rd"
      )

      val validCreateForumDetalis = Seq(
        "name" -> "subforum name",
        "moderators" -> "admin@mailmail.com" //Todo: adding support to more than one moderator.
      )

      route(FakeRequest(POST, registerEndpoint).withFormUrlEncodedBody(validModerators2Details: _*))
      val response = route(FakeRequest(POST, createSubForumEndpoint).withFormUrlEncodedBody(validCreateForumDetalis: _*)).get

      status(response) must be_==(OK)
      contentAsString(response) must /("subforum_id" -> ".+".r)

    }

    "Fail if invalid details" in new WithApplication() {
      val invalidCreateForumDetalis = Seq(
        "name" -> "subforum name",
        "moderators" -> "no-mail" //Todo: adding support to more than one moderator.
      )

      val response = route(FakeRequest(POST, createSubForumEndpoint).withFormUrlEncodedBody(invalidCreateForumDetalis: _*)).get
      status(response) must be_==(BAD_REQUEST)
      contentAsString(response) must /("error" -> "Invalid data")
    }

  }




}
