package com.gamesofforums.domain

import com.gamesofforums.domain.Policies.PasswordPolicy
import com.gamesofforums.exceptions.{InvalidDataException, RegistrationException}
import com.twitter.util.{Return, Throw}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/5/15.
 */
class UsersTests extends Specification {
  trait Ctx extends Scope {
    val usersManager = new UsersManager()
  }

  "User registration" should {
    "success for a valid user and password" in new Ctx {
      val firstName = "firstName"
      val lastName = "lastName"
      val someEmail = "someEmail@someDomain.com"
      val somePass = "somePass"

      usersManager.register(firstName, lastName, someEmail, somePass) must be_==(Return(someEmail))
    }

    "failed if user already registered" in new Ctx {
      val sameMail = "someEmail@someDomain.com"
      usersManager.register("asdf", "xcv", sameMail, "somePass")

      usersManager.register("asdasas", "dfbdfg", sameMail, "somePass") must be_==(Throw(RegistrationException("Duplicate mail")))
    }

    "failed for an invalid mail" in new Ctx {
      usersManager.register("asdf", "xcv", "", "somePass") must be_==(Throw(InvalidDataException("Invalid mail")))
    }

    "failed for an invalid password (doesn't meet the current password policy)" in {
      val userManagerWithPolicy = new UsersManager(Some(new PasswordPolicy {
        override def isValid(password: String): Boolean = password.length > 1
      }))

      userManagerWithPolicy.register("guy", "gonen", "guyg@gmail.com", "") must be_==(Throw(InvalidDataException("Invalid password")))
    }
  }
}
