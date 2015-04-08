package com.gamesofforums.domain

import com.gamesofforums.exceptions.TopicException

/**
 * Created by Guy Gonen on 08/04/2015.
 */
case class Topic(title: String, content: String) {
  if (title.isEmpty || content.isEmpty) throw TopicException("Invalid input.")

}
