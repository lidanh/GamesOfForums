package com.gamesofforums

import java.util.UUID

/**
 * Created by lidanh on 5/10/15.
 */
package object domain {
  type IdType = String

  def generateId: IdType = UUID.randomUUID().toString
}
