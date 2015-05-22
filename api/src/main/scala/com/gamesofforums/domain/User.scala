package com.gamesofforums.domain

import com.wix.accord.Validator
import com.wix.accord.dsl.{validator => accordValidator, _}

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

/**
 * Created by lidanh on 4/5/15.
 */
case class User(id: IdType = generateId,
                firstName: String,
                lastName: String,
                mail: String,
                password: String,
                private var _role: Role = NormalUser,
                verificationCode: Option[String] = None) extends ValidationSupport {

  val messages = ListBuffer[Message]()
  override implicit val validator: Validator[User] = User.validator

  def notify(message: Message): Unit = {
    // Todo: notify user
  }

  def role = _role

  def is(newRole: Role) = {
    if (role.isInstanceOf[Moderator] && newRole.isInstanceOf[Moderator]) {
      // mix the forums the user moderates
      _role = Moderator(this.role.asInstanceOf[Moderator].at ++ newRole.asInstanceOf[Moderator].at)
    } else {
      _role = newRole
    }
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