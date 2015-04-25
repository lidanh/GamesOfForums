package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/18/15.
 */
class UserTests extends Specification with ResultMatchers {
  val validUser = User("bibi", "buzi", "someone@gmail.com", "1234")
  
  "User" should {
    "be valid with firstname, lastname, email and password" in {
      validUser.validate should succeed
    }
    
    "be invalid without first name" in {
      validUser.copy(firstName = "").validate should failWith("first name" -> "must not be empty")
    }

    "be invalid without last name" in {
      validUser.copy(lastName = "").validate should failWith("last name" -> "must not be empty")
    }

    "be invalid with invalid email" in {
      validUser.copy(mail = "blabla").validate should failWith("mail" -> s"must match regular expression '${User.mailPattern}'")
    }

    "be invalid without password" in {
      validUser.copy(password = "").validate should failWith("password" -> "must not be empty")
    }
  }

  "Set user role" should {
    "return a new user object with the given role" in {
      (validUser is God).role should beEqualTo(God)
    }
  }
}
