package com.gamesofforums.domain

import com.shingimmel.dsl.AuthorizationRules

/**
 * Created by lidanh on 4/19/15.
 */

trait Role extends Ordered[Role] {
  implicit val authRules: AuthorizationRules[User]

  override def compare(that: Role): Int = {
    // comparison based on their permissions count
    this.authRules.permissions.size - that.authRules.permissions.size
  }
}

trait RulesPredicates {
  def heOwnsTheMessage = (user: User, message: Message) => (user.messages.contains(message))

  def itsAValidModeratorMessage = { (user: User, message: Message) =>
    user.messages.contains(message) || user.role.asInstanceOf[Moderator].at.contains(message.rootPost.postedIn)
  }
}