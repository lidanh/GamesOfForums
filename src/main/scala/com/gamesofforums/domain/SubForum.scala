package com.gamesofforums.domain

import com.wix.accord.dsl.{validator => accordValidator, _}
import com.wix.accord.{Result, Validator, validate => accordValidate}

import scala.collection.mutable.ListBuffer

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class SubForum(name: String, moderators: Seq[User]) extends ValidationSupport {
  val posts = ListBuffer[Post]()
  override implicit val validator: Validator[SubForum] = SubForum.validator

  def validate(forumPolicy: ForumPolicy): Result = super.validate and accordValidate(this)(forumPolicy.subforumPolicy)
}

object SubForum {
  implicit val validator = accordValidator[SubForum] { subforum =>
    subforum.name is notEmpty
  }
}
