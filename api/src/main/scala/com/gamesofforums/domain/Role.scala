package com.gamesofforums.domain

import com.shingimmel.dsl.AuthorizationRules

/**
 * Created by lidanh on 4/19/15.
 */

trait Role {
  implicit val authRules: AuthorizationRules[User]
}

trait RulesPredicates {
  def heOwnsTheMessage = (user: User, message: Message) => (user.messages.contains(message))

  def itsAValidModeratorMessage = { (user: User, message: Message) =>
    // todo: refactor! bad design!
    user.messages.contains(message) || message.rootPost.postedIn.moderators.contains(user)
  }
}