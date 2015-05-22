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

class TableContainer[T]() extends mutable.Seq[T] {
  private val dataStructure = ListBuffer[T]()

  override def filter(p: T => Boolean) = dataStructure.filter(p)
  override def find(p: T => Boolean) = dataStructure.find(p)
  override def exists(p: T => Boolean) = dataStructure.exists(p)
  override def update(idx: Int, elem: T): Unit = dataStructure.update(idx, elem)
  override def length: Int = dataStructure.length
  override def apply(idx: Int): T = dataStructure.apply(idx)
  override def iterator: Iterator[T] = dataStructure.iterator

  def +=(x: T) = dataStructure += x
  def -=(x: T) = dataStructure -= x
}

object TableContainer {
  def apply[T]() = new TableContainer[T]()
}

class InMemoryStorage extends ForumStorage {
  override val subforums = TableContainer[SubForum]()
  override val users = TableContainer[User]()
  override val reports = TableContainer[Report]()
  override val messages =  new TableContainer[Message]() {
    override def +=(elem: Message) = {
      val result = super.+=(elem)

      elem match {
        case m: Post => {
          m.postedIn.messages += m
          m.postedBy.messages += m
        }
        case m: Comment => {
          m.parent.comments += m
          m.rootPost.postedIn.messages += m
        }
      }

      result
    }
  }
}