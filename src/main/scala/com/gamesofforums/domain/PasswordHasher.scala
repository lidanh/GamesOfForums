package com.gamesofforums.domain

/**
 * Created by lidanh on 4/18/15.
 */
trait PasswordHasher {
  def hash(s: String): String
}

object SHA1Hash extends PasswordHasher {
  override def hash(s: String): String = {
    val passDigest = java.security.MessageDigest.getInstance("SHA-1")
    passDigest.digest(s.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }
}
