package com.gamesofforums.domain.Policies

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class ForumPolicy() {
  val subForumPolicy = SubForumPolicy()
  var passwordPolicy = PasswordPolicy.getPasswordPolicy("medium")

}
