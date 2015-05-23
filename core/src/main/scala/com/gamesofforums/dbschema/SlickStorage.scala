package com.gamesofforums.dbschema

import com.gamesofforums.{Repository, domain, ForumStorage}
import com.gamesofforums.domain._
import slick.dbio.{NoStream, DBIOAction}

import scala.concurrent.{Future, Await}
import slick.driver.H2Driver.api._
import scala.language.postfixOps



import scala.concurrent.duration._

/**
 * Created by lidanh on 5/10/15.
 */
class SlickStorage extends ForumStorage {
  private val dbSession = Database.forConfig(SlickStorage.ConfigKeyName)

  val subforumsStorage = SubForums
  val usersStorage = Users
  val moderatorsXsubforums = ModeratorsXSubForums

  private val setupAction: DBIO[Unit] = DBIO.seq(
    (
      subforumsStorage.schema ++
      usersStorage.schema ++
      moderatorsXsubforums.schema
    ).create
  )

  dbSession.run(setupAction)

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

  private def run[R](a: DBIOAction[R, NoStream, Nothing]) = Await.result(dbSession.run(a), SlickStorage.timeout)

  override def addSubforum(subforum: SubForum): Unit = run {
    subforumsStorage += subforum
  }

  override def getSubforum(id: IdType): Option[SubForum] = run {
    subforumsStorage.findById(id).result
  }.headOption

  override def deleteSubforum(id: IdType): Unit = run {
    subforumsStorage.findById(id).delete
  }

  override def subforums: Seq[SubForum] = run {
    subforumsStorage.result
  }

  override def addUser(user: User): Unit = run {
    usersStorage += user
  }

  override def getUser(mail: String): Option[User] = run {
    usersStorage.findByMail(mail).result
  }.headOption

  override def users: Seq[User] = run {
    usersStorage.result
  }
}

object SlickStorage {
  val ConfigKeyName = "db"
  val timeout = 5 seconds
}
//
//object Test extends App {
//  val db = new SlickStorage
//
//  val sf = SubForum(name = "LIDAN HIFI")
//  val anotherSubforum = SubForum(name = "guy")
//
//  db.subforums += sf
//
//  db.run(db.subforums ++= Seq(anotherSubforum, sf))
//
////  val d = db.subforums.findById(db.sf.id).delete
////  db.run(d)
//
//  val f = db.run(db.subforums.result)
//
//  Await.result(f, 5 seconds).foreach(println)
//}