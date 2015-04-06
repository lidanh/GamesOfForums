package com.gamesofforums.domain

import com.gamesofforums.domain.Policies.PasswordPolicy
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 06/04/2015.
 */
class PasswordPolicyTests extends Specification {


  trait weakPasswordTests extends Scope {
    val passPolicy = PasswordPolicy.getPasswordPolicy("weak")
  }

  trait mediumPasswordTests extends Scope {
    val passPolicy = PasswordPolicy.getPasswordPolicy("medium")
  }

  trait bestPasswordTests extends Scope {
    val passPolicy = PasswordPolicy.getPasswordPolicy("best")
  }

  "PasswordPolicy" should {
    "Success for weak password policy" in new weakPasswordTests {
      val weakPassword = "123";
      passPolicy.isValid(weakPassword) must beTrue
    }

    "Failure for no password in weak policy" in new weakPasswordTests {
      val nullPassword = "";
      passPolicy.isValid(nullPassword) must beFalse
    }

    "Success for medium password policy" in new mediumPasswordTests {
      val mediumPassword = "1234567";
      passPolicy.isValid(mediumPassword) must beTrue
    }

    "Failure for weak password in medium policy" in new mediumPasswordTests {
      val weakPassword = "123";
      passPolicy.isValid(weakPassword) must beFalse
    }

    "Success for best password policy" in new bestPasswordTests {
      val bestPassword = "1234567987654321";
      passPolicy.isValid(bestPassword) must beTrue
    }

    "Failure for medium password in best policy" in new bestPasswordTests {
      val mediumPassword = "1234567";
      passPolicy.isValid(mediumPassword) must beFalse
    }
  }

}
