package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
object ForumAdmin extends Role {
  override implicit val authRules: AuthorizationRules[User] = rulesFor[User] {
    derivedFrom(NormalUser.authRules)

    can(Ban).a[User]
    can(EditMessages)
    can(DeleteMessages)
    can(ManageSubForumModerators)
    can(ManageForumAdmins)
    can(ManageSubForums)
  }
}
