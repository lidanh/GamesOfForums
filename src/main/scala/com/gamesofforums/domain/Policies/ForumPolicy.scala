package com.gamesofforums.domain.policies

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class ForumPolicy() {
  val subForumPolicy = SubForumPolicy(1, 3)
  val passwordPolicy = PasswordPolicy.getPasswordPolicy("medium")

  def getSubForumPolicy(): SubForumPolicy = subForumPolicy

  def getPasswordPolicy(): PasswordPolicy = passwordPolicy

  def setPasswordPolicy(strength: String): PasswordPolicy = {
    if (passwordPolicy.getPolicyType != strength) PasswordPolicy.getPasswordPolicy(strength)
    passwordPolicy
  }

  def setSubForumPolicy(newMax: Int, newMin: Int): SubForumPolicy = {
    if (subForumPolicy.getMaxModerators() != newMax &&
      subForumPolicy.getMinModerators() != newMin)
      SubForumPolicy(newMin, newMax)
    subForumPolicy
  }

}
