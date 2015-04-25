package com.gamesofforums.domain

import java.util.{Calendar, Date}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
 * Created by Guy Gonen on 08/04/2015.
 */
abstract class Message(val content: String, val postedBy: User, createdAt: Date = Calendar.getInstance().getTime) extends ValidationSupport {
  val comments = ListBuffer[Comment]()

  lazy val rootPost: Post = {

    @tailrec
    def rootRec(message: Message): Post = {
      message match {
        case p: Post => p
        case c: Comment => rootRec(c.parent)
      }
    }

    rootRec(this)
  }
}
