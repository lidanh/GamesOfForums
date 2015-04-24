package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
case class Moderator() extends Role with RulesPredicates {
  override implicit val authRules: AuthorizationRules[User] = Moderator.acl
}

object Moderator {
  val acl = rulesFor[User] {
    derivedFrom(NormalUser.acl)

    can(Ban).a[User]

    can(EditMessages) onlyWhen { (user: User, message: Message) =>
      // todo: refactor the design!
      user.messages.contains(message) || message.rootPost.postedIn.moderators.contains(user)
    }

    can(DeleteMessages) onlyWhen { (user: User, message: Message) =>
      user.messages.contains(message) || message.rootPost.postedIn.moderators.contains(user)
    }
  }
}