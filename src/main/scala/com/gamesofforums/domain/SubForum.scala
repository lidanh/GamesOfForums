package com.gamesofforums.domain

import com.gamesofforums.exceptions.{PostException, SubForumException}
import com.twitter.util.Try

/**
 * Created by Guy Gonen on 05/04/2015.
 */
case class SubForum (forumName:String, moderators:List[String]){
  if (invalidSubforumInput()) throw (SubForumException("Invalid input: Creating subforum."))
  var posts = scala.collection.mutable.Map[String, Post]()

  def publishNewPost(title: String, content: String): Try[String] = {
    Try {
      if (posts.contains(title)) throw PostException("Duplicated topic.")
      posts.put(title, Post(title, content))
      title
    }
  }

  def deletePost(title: String): Try[Unit] = {
    Try {
      if (!posts.contains(title)) throw SubForumException("Posts didn't exist or already deleted.")
      posts.foreach {
        case (key, post) => post.deleteAllComments()
      }
      posts.remove(title)
    }
  }

  def deleteAllPosts(): Unit = {
    posts.foreach {
      case (key, post) => post.deleteAllComments()
    }
    posts.clear()
  }

  def watchPost(title: String): Option[Post] = posts.get(title)

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

