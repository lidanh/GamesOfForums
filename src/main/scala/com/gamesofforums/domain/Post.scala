package com.gamesofforums.domain

import com.wix.accord.Validator
import com.wix.accord.dsl.{validator => accordValidator, _}

import scala.collection.mutable

/**
 * Created by Guy Gonen on 08/04/2015.
 */
case class Post(subject: String, content: String, postedBy: User) extends Message(content, postedBy) {
  val subscribers = mutable.Set[User]()

  override implicit val validator: Validator[Post] = Post.validator
}

object Post {
  implicit val validator = accordValidator[Post] { post =>
    post.subject is notEmpty
    post.content is notEmpty
    post.postedBy is valid
  }
}
