package com.gamesofforums.domain

import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
case class Moderator(at: Seq[SubForum]) extends Role with RulesPredicates {
  def this(in: SubForum) = this(Seq(in))

  // add itself to subforums' moderators
  at.foreach(_._moderators += this)

  override implicit val authRules: AuthorizationRules[User] = rulesFor[User] {
    derivedFrom(NormalUser.authRules)

    can(Ban).a[User]

    can(EditMessages) onlyWhen itsAValidModeratorMessage
    can(DeleteMessages) onlyWhen itsAValidModeratorMessage
  }
}

object Moderator {
  def apply(at: SubForum) = new Moderator(at)
}