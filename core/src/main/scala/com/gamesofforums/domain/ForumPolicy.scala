package com.gamesofforums.domain

import com.gamesofforums.domain.PasswordPolicy.MediumPasswordPolicy
import com.wix.accord.dsl._

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class ForumPolicy(passwordPolicy: PasswordPolicy = MediumPasswordPolicy,
                       minModerators: Int = 1,
                       maxModerators: Int = 3) {

  def subforumPolicy = validator[SubForum] { subforum =>
    subforum._moderators.length as "moderators count" is between(minModerators, maxModerators)
  }
}
