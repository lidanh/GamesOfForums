package com.gamesofforums.domain

import com.gamesofforums.domain.Policies.ForumPolicy
import com.gamesofforums.exceptions.{ForumCreationException, SubForumCreationException}
import com.twitter.util.Try

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class Forum (forumName : String, policy: ForumPolicy){
  if (invalidInput(forumName, policy)) throw (ForumCreationException("Invalid input: Creating forum."))
  val subForums = scala.collection.mutable.Map[String, SubForum]()

  def createNewSubforum (subforumName : String, moderators : List[String]): Try[String] ={
    Try {
      // check duplication
      if (subForums.contains(subforumName)) throw SubForumCreationException("Duplicate forum names")

      subForums.put(subforumName, SubForum(subforumName, moderators))

      subforumName
    }
  }

  def invalidInput(forumName : String, policy: ForumPolicy) : Boolean =  {
    return (invalidName(forumName) || invalidPolicy(policy))
  }

  def invalidName (forumName : String) : Boolean = {
    if (forumName.size<2) return true
    false
  }

  def invalidPolicy(policy: ForumPolicy) : Boolean = {
    if(policy==null) return true;
    false
  }


}
