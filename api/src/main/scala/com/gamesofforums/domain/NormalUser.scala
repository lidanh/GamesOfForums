package com.gamesofforums.domain

/**
 * Created by lidanh on 4/24/15.
 */

object NormalUser extends Role with RulesPredicates {
  import com.shingimmel.dsl._

  override implicit val authRules: AuthorizationRules[User] = rulesFor[User] {
    can(Publish)
    can(ReportUsers)
    can(EditMessages) onlyWhen heOwnsTheMessage
    can(DeleteMessages) onlyWhen heOwnsTheMessage
  }
}
