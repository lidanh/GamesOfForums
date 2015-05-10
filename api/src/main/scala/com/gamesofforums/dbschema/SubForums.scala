package com.gamesofforums.dbschema

import com.gamesofforums.domain.generateId
import com.gamesofforums.domain.SubForum
import slick.driver.H2Driver.api._

/**
 * Created by lidanh on 5/10/15.
 */
class SubForums(tag: Tag) extends Table[SubForum](tag, "subforums") {
  def id = column[String]("id", O.PrimaryKey, O.Default(generateId))
  def name = column[String]("name")

  def * = (id, name) <> ((SubForum.apply _).tupled, SubForum.unapply)
}