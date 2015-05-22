package com.gamesofforums.dbschema

import com.gamesofforums.domain.{IdType, SubForum}
import slick.driver.H2Driver.api._

/**
 * Created by lidanh on 5/10/15.
 */
trait IdColumn { this: Table[_] =>
  def id = column[IdType]("id", O.PrimaryKey)
}

class SubForums(tag: Tag) extends Table[SubForum](tag, "subforums") with IdColumn {
  def name = column[String]("name")

  def * = (id, name) <> ((SubForum.apply _).tupled, SubForum.unapply)
}

object SubForums extends TableQuery(new SubForums(_)) {
  val findById = this.findBy(_.id)
}