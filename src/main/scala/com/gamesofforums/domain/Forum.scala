package com.gamesofforums.domain

import com.gamesofforums.domain.Policies.ForumPolicy
import com.gamesofforums.exceptions.{ForumException, SubForumException}
import com.twitter.util.Try

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class Forum (forumName : String, policy: ForumPolicy){
  if (invalidInput(forumName, policy)) throw (ForumException("Invalid input: Creating forum."))
  var subForums = scala.collection.mutable.Map[String, SubForum]()

  def createNewSubforum (subforumName : String, moderators : List[String]): Try[String] ={
    Try {
      // check duplication
      if (subForums.contains(subforumName)) throw SubForumException("Duplicate forum names")

      subForums.put(subforumName, SubForum(subforumName, moderators))

      subforumName
    }
  }

  def deleteSubForum(subForumName: String): Try[Unit] = {
    Try {
      if (!subForums.contains(subForumName)) throw SubForumException("SubForum didn't exist or already deleted.")
      subForums.foreach {
        case (key, subForum) => subForum.deleteAllPosts()
      }
      subForums.remove(subForumName)
    }
  }

  def watchSubForumsList(): List[SubForum] = {
    subForums.values.toList
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
