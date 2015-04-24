package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
object Moderator extends Role {
  override implicit val authRules: AuthorizationRules[User] = rulesFor[User] {
    derivedFrom(NormalUser.authRules)

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