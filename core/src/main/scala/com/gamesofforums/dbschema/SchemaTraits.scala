package com.gamesofforums.dbschema

import com.gamesofforums.domain.IdType
import slick.driver.H2Driver.api._

/**
 * Created by lidanh on 5/22/15.
 */
trait IdColumn {
  this: Table[_] =>
  def id = column[IdType]("id", O.PrimaryKey)
}
