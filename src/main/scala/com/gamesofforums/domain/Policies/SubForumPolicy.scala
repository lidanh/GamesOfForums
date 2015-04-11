package com.gamesofforums.domain.policies

import com.gamesofforums.exceptions.ForumPolicyException
import com.twitter.util.Try

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class SubForumPolicy(var minNumOfModerators: Int, var maxNumOfModerators: Int) {
  if (maxNumOfModerators < minNumOfModerators) throw ForumPolicyException("Min is higher than max")


  def getMaxModerators(): Int = maxNumOfModerators

  def setMaxModerators(newMax: Int) = {
    Try {
      if (newMax < minNumOfModerators) throw ForumPolicyException("New max lower than current min")
      maxNumOfModerators = newMax
    }
  }

  def getMinModerators(): Int = minNumOfModerators

  def setMinModerators(newMin: Int) = {
    Try {
      if (newMin > maxNumOfModerators) throw ForumPolicyException("New min lower than current max")
      minNumOfModerators = newMin
    }
  }
}
