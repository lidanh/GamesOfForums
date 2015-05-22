package com.gamesofforums.domain

import com.wix.accord.Validator
import com.wix.accord.dsl.{validator => accordValidator, _}

/**
 * Created by Guy Gonen on 08/04/2015.
 */
case class Comment(override val id: IdType = generateId,
                   override val content: String,
                   parent: Message,
                   override val postedBy: User) extends Message(id, content, postedBy) {
  override val validator: Validator[Comment] = Comment.validator
}

object Comment {
  val validator = accordValidator[Comment] { comment =>
    comment.content is notEmpty
  }
}