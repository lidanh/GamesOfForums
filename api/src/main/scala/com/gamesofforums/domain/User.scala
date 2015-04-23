package com.gamesofforums.domain

import com.wix.accord.Validator
import com.wix.accord.dsl.{validator => accordValidator, _}

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

/**
 * Created by lidanh on 4/5/15.
 */
case class User(firstName: String, lastName: String, mail: String, password: String) extends ValidationSupport {
  val posts = ListBuffer[Post]()
  override implicit val validator: Validator[User] = User.validator

  def notify(message: Message): Unit = {
    // Todo: notify user
  }
}

object User {
  implicit val validator = accordValidator[User] { user =>
    user.firstName as "first name" is notEmpty
    user.lastName as "last name" is notEmpty
    user.mail should matchRegex(User.mailPattern)
    user.password is notEmpty
  }

  val mailPattern: Regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$".r
}