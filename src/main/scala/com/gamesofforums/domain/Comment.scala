package com.gamesofforums.domain

import com.wix.accord.Validator
import com.wix.accord.dsl.{validator => accordValidator, _}

/**
 * Created by Guy Gonen on 08/04/2015.
 */
case class Comment(content: String, parent: Message, postedBy: User) extends Message(content, postedBy) {
  override val validator: Validator[Comment] = Comment.validator
  parent.comments += this
}

object Comment {
  val validator = accordValidator[Comment] { comment =>
    comment.content is notEmpty
  }
}