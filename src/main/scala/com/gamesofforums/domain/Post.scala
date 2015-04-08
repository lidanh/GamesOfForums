package com.gamesofforums.domain

import com.gamesofforums.domain.Policies.Message
import com.gamesofforums.exceptions.TopicException
import com.twitter.util.Try

import scala.collection.mutable


/**
 * Created by Guy Gonen on 08/04/2015.
 */
case class Post(title: String, content: String) extends Message {
  if (title.isEmpty || content.isEmpty) throw TopicException("Invalid input.")

  var postComments = mutable.LinkedHashMap[String, Comment]()

  def getContent(): String = content

  def newCommentOnPost(content: String): Try[String] = {
    Try {
      postComments.put(content, Comment(content))
      content
    }
  }

}
