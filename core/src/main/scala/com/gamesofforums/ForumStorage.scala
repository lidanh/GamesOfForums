package com.gamesofforums

import com.gamesofforums.domain._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import reflect.runtime.universe._

/**
 * Created by lidanh on 5/10/15.
 */
trait ForumStorage {
  def addUser(user: User): Unit
  def getUser(mail: String): Option[User]

  def addSubforum(subforum: SubForum): Unit
  def getSubforum(id: IdType): Option[SubForum]
  def deleteSubforum(id: IdType): Unit

  def subforums: Seq[SubForum]
  def users: Seq[User]
}

class Repository[T]() extends mutable.Seq[T] {
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

object Repository {
  def apply[T]() = new Repository[T]()
}

class InMemoryStorage extends ForumStorage {
  val subforumsStorage = Repository[SubForum]()
  val usersStorage = Repository[User]()

  val reportsStorage = Repository[Report]()
  val messagesStorage =  new Repository[Message]() {
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

  val types = Map[Class[_], Repository[_]](
    classOf[User] -> usersStorage,
    classOf[SubForum] -> subforumsStorage
  )

  def tableOf[T : TypeTag](obj: T): Repository[_] = obj match {
    case x: User => usersStorage
    case x: SubForum => subforumsStorage
  }

  override def addSubforum(subforum: SubForum): Unit = subforumsStorage += subforum

  override def getSubforum(id: IdType): Option[SubForum] = subforumsStorage.find(_.id == id)

  override def deleteSubforum(id: IdType): Unit = getSubforum(id).foreach(s => subforumsStorage -= s)

  override def subforums: Seq[SubForum] = subforumsStorage.toSeq

  override def addUser(user: User): Unit = usersStorage += user

  override def getUser(mail: String): Option[User] = usersStorage.find(_.mail == mail)

  override def users: Seq[User] = usersStorage.toSeq
}