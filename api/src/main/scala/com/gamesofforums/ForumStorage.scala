package com.gamesofforums

import com.gamesofforums.domain._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by lidanh on 5/10/15.
 */
trait ForumStorage {
  val subforums: Any
  val users: Any
  val reports: Any
  val messages: Any
}

class InMemoryStorage extends ForumStorage {
  override val subforums = new ListBuffer[SubForum]()
  override val users = ListBuffer[User]()
  override val reports = ListBuffer[Report]()
  override val messages =  new mutable.UnrolledBuffer[Message]() {
    override def +=(elem: Message): this.type = {
      val result = super.+=(elem)

      elem match {
        case m: Post => {
          m.postedIn.messages += m
          m.postedBy.messages += m
        }
      }

      result
    }
    
    override def -=(x: Message): this.type = super.-=(x)
  }
}