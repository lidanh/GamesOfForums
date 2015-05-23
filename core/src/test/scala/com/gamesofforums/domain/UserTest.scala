package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/18/15.
 */
class UserTest extends Specification with ResultMatchers {
  trait Ctx extends Scope {
    val validUser = User(
      generateId,
      firstName = "bibi",
      lastName = "buzi",
      mail = "someone@gmail.com",
      password = "1234")
  }
  
  "User" should {
    "be valid with firstname, lastname, email and password" in new Ctx {
      validUser.validate should succeed
    }
    
    "be invalid without first name" in new Ctx {
      validUser.copy(firstName = "").validate should failWith("first name" -> "must not be empty")
    }

    "be invalid without last name" in new Ctx {
      validUser.copy(lastName = "").validate should failWith("last name" -> "must not be empty")
    }

    "be invalid with invalid email" in new Ctx {
      validUser.copy(mail = "blabla").validate should failWith("mail" -> s"must match regular expression '${User.mailPattern}'")
    }

    "be invalid without password" in new Ctx {
      validUser.copy(password = "").validate should failWith("password" -> "must not be empty")
    }
  }

  "Set user role" should {
    "change user's role if stronger than the current one" in new Ctx {
      val user = validUser.copy(_role = NormalUser)

      user is God

      user.role should beEqualTo(God)
    }

    "change user's role if weaker than the current one" in new Ctx {
      val user = validUser.copy(_role = God)

      user is NormalUser

      user.role should beEqualTo(NormalUser)
    }

    "moderate the forums of the new role with the previous one, if both of them are moderation" in new Ctx {
      val subforum1 = SubForum(generateId, name = "sf1")
      val subforum2 = SubForum(generateId, name = "sf2")

      validUser is Moderator(subforum1)
      validUser is Moderator(subforum2)

      validUser.role must beAnInstanceOf[Moderator]
      validUser.role.asInstanceOf[Moderator].at must be_==(Seq(subforum1, subforum2))
    }
  }
}
