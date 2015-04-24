package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
case class ForumAdmin() extends Role {
  override implicit val authRules: AuthorizationRules[User] = ForumAdmin.acl
}

object ForumAdmin {
  val acl = rulesFor[User] {
    derivedFrom(NormalUser.acl)

    can(Ban).a[User]
    can(EditMessages)
    can(DeleteMessages)
    can(ManageSubForumModerators)
    can(ManageForumAdmins)
    can(ManageSubForums)
  }
}
