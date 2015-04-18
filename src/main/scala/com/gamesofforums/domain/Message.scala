package com.gamesofforums.domain

import java.util.{Calendar, Date}

import scala.collection.mutable.ListBuffer

/**
 * Created by Guy Gonen on 08/04/2015.
 */
abstract class Message(content: String, postedBy: User, createdAt: Date = Calendar.getInstance().getTime) extends ValidationSupport {
  val comments = ListBuffer[Comment]()
}
