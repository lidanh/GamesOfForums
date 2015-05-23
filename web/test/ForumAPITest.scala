import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import org.specs2.matcher.JsonMatchers

/**
 * Created by lidanh on 5/23/15.
 */
class ForumAPITest extends Specification with JsonMatchers {

  val baseEndpoint = "/api"

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
        "mail" -> "gg",
        "password" -> "p@ssw0rd"
      )
      val response = route(FakeRequest(POST, registerEndpoint).withFormUrlEncodedBody(invalidDetails: _*)).get

      status(response) must be_==(BAD_REQUEST)
      contentAsString(response) must /("error" -> "Invalid data")
    }
  }
}
