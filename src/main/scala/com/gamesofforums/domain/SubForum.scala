package com.gamesofforums.domain

import com.gamesofforums.exceptions.{ForumCreationException, InvalidDataException}
import com.twitter.util.Throw

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class SubForum (forumName:String, moderators:List[String]){
  if (invalidSubforumInput()) throw (ForumCreationException("Invalid input: Creating subforum."))

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

