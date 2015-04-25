package com.gamesofforums.domain

import com.wix.accord.Validator
import com.wix.accord.dsl.{validator => accordValidator, _}

import scala.annotation.tailrec

/**
 * Created by Guy Gonen on 08/04/2015.
 */
case class Comment(override val content: String, parent: Message, override val postedBy: User) extends Message(content, postedBy) {
  override val validator: Validator[Comment] = Comment.validator
}

object Comment {
  val validator = accordValidator[Comment] { comment =>
    comment.content is notEmpty
  }
}