package com.gamesofforums.domain

import com.shingimmel.dsl.AuthorizationRules

/**
 * Created by lidanh on 4/24/15.
 */
case class NormalUser() extends Role {
  override implicit val authRules: AuthorizationRules[User] = NormalUser.acl
}

object NormalUser extends RulesPredicates {
  import com.shingimmel.dsl._

  val acl = rulesFor[User] {
    can(Publish)
    can(EditMessages) onlyWhen heOwnsTheMessage
    can(DeleteMessages) onlyWhen heOwnsTheMessage
  }
}
