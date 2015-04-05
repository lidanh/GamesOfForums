package com.gamesofforums.domain

import com.gamesofforums.exceptions.{ForumCreationException, RegistrationException, InvalidDataException}
import com.twitter.util.Try

/**
 * Created by Guy Gonen on 05/04/2015.
 */
class Forum (forumName : String){

  val subForums = scala.collection.mutable.Map[String, SubForum]()

  def createNewSubforum (subforumName : String, moderators : List[String]): Try[String] ={
    Try {
      // check duplication
      if (subForums.contains(subforumName)) throw ForumCreationException("Duplicate forum names")

      subForums.put(subforumName, SubForum(subforumName, moderators))

      subforumName
    }
  }

}
