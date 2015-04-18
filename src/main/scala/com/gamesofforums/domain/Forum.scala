package com.gamesofforums.domain

import scala.collection.mutable.ListBuffer

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class Forum(policy: ForumPolicy) {
  val users = ListBuffer[User]()
  val subForums = ListBuffer[SubForum]()
}
