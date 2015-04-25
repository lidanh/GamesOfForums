package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
object God extends Role {
  override implicit val authRules: AuthorizationRules[User] = rulesFor[User] {
    derivedFrom(ForumAdmin.authRules)

    can(ManageForumPolicy)
    can(ManageUserTypes)
  }
}
