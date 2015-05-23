package com.gamesofforums.dbschema

import com.gamesofforums.domain._
import slick.driver.H2Driver.api._

/**
 * Created by lidanh on 5/22/15.
 */
class ModeratorsXSubForums(tag: Tag) extends Table[(IdType, IdType)](tag, "moderations") {
  // table attributes
  def moderatorId = column[IdType]("user_id")
  def subforumId = column[IdType]("subforum_id")

  // composite primary key
  def pk = primaryKey("pk_moderations", (moderatorId, subforumId))

  def fkUsers = foreignKey("fk_users", moderatorId, Users)(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  def fkSubforums = foreignKey("fk_subforums", subforumId, SubForums)(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (moderatorId, subforumId)
}

object ModeratorsXSubForums extends TableQuery(new ModeratorsXSubForums(_))