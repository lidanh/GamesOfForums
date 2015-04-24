package com.gamesofforums.domain

import com.shingimmel.AuthorizationRules
import com.shingimmel.dsl._
import com.shingimmel.Permission

/**
 * Created by lidanh on 4/19/15.
 */
trait Role {
  implicit val authRules: AuthorizationRules
}

case class NormalUser() extends Role {
  override implicit val authRules = rulesFor {
    can(Publish)
    can(EditMessages) // when user owns the message
    can(DeleteMessages)
  }
}
