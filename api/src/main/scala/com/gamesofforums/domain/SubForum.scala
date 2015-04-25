package com.gamesofforums.domain

import com.wix.accord.dsl.{validator => accordValidator, _}
import com.wix.accord.{Result, Validator, validate => accordValidate}

import scala.collection.mutable.ListBuffer

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class SubForum(name: String, private[domain] val _moderators: ListBuffer[Moderator] = ListBuffer.empty) extends ValidationSupport {
  val messages = ListBuffer[Message]()

  override implicit val validator: Validator[SubForum] = SubForum.validator

  def validate(forumPolicy: ForumPolicy): Result = super.validate and accordValidate(this)(forumPolicy.subforumPolicy)

  def moderators = _moderators

  // Workaround for _moderators (a circular reference causes the test to crash)
  override def toString: String = s"SubForum($name)"
}

object SubForum {
  implicit val validator = accordValidator[SubForum] { subforum =>
    subforum.name is notEmpty
  }
}
