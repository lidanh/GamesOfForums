package com.gamesofforums.domain


import com.wix.accord.dsl.{validator => accordValidator, _}
import com.wix.accord.{Validator, validate => accordValidate}

trait PasswordPolicy {
  implicit val validator: Validator[String]

  def validate(password: String) = accordValidate(password).withDescription("password")
}

object PasswordPolicy {
  object HardPasswordPolicy extends PasswordPolicy {
    implicit val validator = accordValidator[String] { password =>
      password have size >= 8
    }
  }

  object MediumPasswordPolicy extends PasswordPolicy {
    implicit val validator = accordValidator[String] { password =>
      password have size >= 6
    }
  }

  object WeakPasswordPolicy extends PasswordPolicy {
    implicit val validator = accordValidator[String] { password =>
      password is notEmpty
    }
  }
}


