package com.gamesofforums.dbschema

import com.gamesofforums.ForumStorage
import com.gamesofforums.domain._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._

/**
 * Created by lidanh on 5/10/15.
 */
class SlickStorage extends ForumStorage {
  override val subforums = TableQuery[SubForums]
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
        case m: Comment => {
          m.parent.comments += m
          m.rootPost.postedIn.messages += m
        }
      }

      result
    }

    override def -=(x: Message): this.type = super.-=(x)
  }

  val db = Database.forConfig(SlickStorage.ConfigKeyName)

  private val setupAction: DBIO[Unit] = DBIO.seq(
    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
    (subforums.schema).create
  )

  db.run(setupAction)
}

object SlickStorage {
  val ConfigKeyName = "db"
}
