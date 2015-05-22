package com.gamesofforums.dbschema

import com.gamesofforums.{TableContainer, domain, ForumStorage}
import com.gamesofforums.domain._
import slick.dbio.{NoStream, DBIOAction}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

/**
 * Created by lidanh on 5/10/15.
 */
class SlickStorage extends ForumStorage {
  private val dbSession = Database.forConfig(SlickStorage.ConfigKeyName)

  val subforumsStorage = SubForums

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

  private def run[R](a: DBIOAction[R, NoStream, Nothing]) = Await.result(dbSession.run(a), SlickStorage.timeout)

  private val setupAction: DBIO[Unit] = DBIO.seq(
    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
    (subforumsStorage.schema).create
  )

  dbSession.run(setupAction)

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