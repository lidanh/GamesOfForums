package com.gamesofforums

import com.gamesofforums.domain.policies.PasswordPolicy
import com.gamesofforums.domain.User
import com.gamesofforums.exceptions.{InvalidDataException, RegistrationException}
import com.twitter.util.Try

/**
 * Created by lidanh on 4/5/15.
 */
class UsersManager(passwordPolicy: Option[PasswordPolicy] = None) {
  val users = scala.collection.mutable.Map[String, User]()

  def register(firstName: String, lastName: String, mail: String, password: String): Try[String] = {
    Try {
      // check password policy
      passwordPolicy.foreach { policy =>
        if (!policy.isValid(password)) throw InvalidDataException("Invalid password")
      }

      // check duplication
      if (users.contains(mail)) throw RegistrationException("Duplicate mail")

      users.put(mail, User(firstName, lastName, mail, password))

      mail
    }
  }
}
