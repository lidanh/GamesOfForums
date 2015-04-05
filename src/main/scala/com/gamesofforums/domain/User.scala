package com.gamesofforums.domain

import com.gamesofforums.exceptions.InvalidDataException

/**
 * Created by lidanh on 4/5/15.
 */
case class User(firstName: String, lastName: String, mail: String, password: String) {
  if (!mail.matches(User.mailPattern)) throw InvalidDataException("Invalid mail")
}

object User {
  val mailPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
}
