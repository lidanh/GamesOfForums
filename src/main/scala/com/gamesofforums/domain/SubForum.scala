package com.gamesofforums.domain

import com.gamesofforums.exceptions.{SubForumCreationException, TopicException}
import com.twitter.util.Try

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class SubForum (forumName:String, moderators:List[String]){
  if (invalidSubforumInput()) throw (SubForumCreationException("Invalid input: Creating subforum."))
  var subForums = scala.collection.mutable.Map[String, Post]()

  def publishNewTopic(title: String, content: String): Try[String] = {
    Try {
      if (subForums.contains(title)) throw TopicException("Duplicated topic.")
      subForums.put(title, Post(title, content))
      title
    }
  }


  def invalidSubforumInput(): Boolean ={
    if (invalidName() || invalidModerators()) return true
    false
  }

  def invalidName () : Boolean = {
    if (forumName.size<2) return true
    false
  }
  def invalidModerators () : Boolean = {
    if (moderators.isEmpty) return true
    false
  }


}

