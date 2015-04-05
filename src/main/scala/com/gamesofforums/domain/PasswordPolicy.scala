package com.gamesofforums.domain

/**
 * Created by lidanh on 4/5/15.
 */
trait PasswordPolicy {
  def isValid(password: String): Boolean
}
