package com.gamesofforums.domain

import com.gamesofforums.domain.Policies.Message
import com.gamesofforums.exceptions.CommentException

/**
 * Created by Guy Gonen on 08/04/2015.
 */
case class Comment(content: String) extends Message {
  if (content.isEmpty) throw CommentException("Invalid comment.")

  def getContent(): String = content


}
