package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
object Moderator extends Role with RulesPredicates {
  override implicit val authRules: AuthorizationRules[User] = rulesFor[User] {
    derivedFrom(NormalUser.authRules)

    can(Ban).a[User]

    can(EditMessages) onlyWhen itsAValidModeratorMessage
    can(DeleteMessages) onlyWhen itsAValidModeratorMessage
  }
}