package com.gamesofforums.domain

import com.shingimmel.AuthorizationRules
import com.shingimmel.dsl._
import com.shingimmel.Permission

/**
 * Created by lidanh on 4/19/15.
 */
trait RulesPredicates {
  def heOwnsTheMessage(user: User, message: Message) = {
    user.messages.contains(message)
  }
}

trait Role {
  implicit val authRules: AuthorizationRules[User]
}

case class NormalUser() extends Role {
  override implicit val authRules: AuthorizationRules[User] = NormalUser.acl
}

object NormalUser extends RulesPredicates {
  val acl = rulesFor[User] {
    can(Publish)
    can(EditMessages) onlyWhen heOwnsTheMessage
    can(DeleteMessages) onlyWhen heOwnsTheMessage
  }
}
