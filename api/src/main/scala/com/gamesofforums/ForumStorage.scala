package com.gamesofforums

import com.gamesofforums.domain.{Report, User, SubForum}

import scala.collection.mutable.ListBuffer

/**
 * Created by lidanh on 5/10/15.
 */
trait ForumStorage {
  val subforums: Any
  val users: Any
  val reports: Any
}

class InMemoryStorage extends ForumStorage {
  override val subforums = ListBuffer[SubForum]()
  override val users = ListBuffer[User]()
  override val reports = ListBuffer[Report]()
}