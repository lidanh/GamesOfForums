package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
case class God() extends Role {
  override implicit val authRules: AuthorizationRules[User] = God.acl
}

object God {
  val acl = rulesFor[User] {
    derivedFrom(ForumAdmin.acl)

    can(ManageForumPolicy)
  }
}
