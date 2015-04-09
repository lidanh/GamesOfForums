package com.gamesofforums.domain

import com.gamesofforums.exceptions.SubForumException
import com.twitter.util.Throw
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 09/04/2015.
 */
class PostDeletionTests extends Specification {

  trait Ctx extends Scope {
    val subForum = new SubForum("Base", List("Tyrion", "Tywin"))
    subForum.publishNewPost("Tyrion", "Tyrion is the best character in game of thrones.")
    subForum.publishNewPost("Jon Snow", "Knows nothing.")
  }

  "Post deletion" should {
    "Success for valid post" in new Ctx {
      val post = "Tyrion"
      subForum.watchPost(post) must_!= (None)
      subForum.deletePost(post)
      subForum.watchPost(post) must be_==(None)
    }

    "Failure for unexisted post" in new Ctx {
      val unExistedPost = "Ned Stark" // Cause he died in the first book, So he doesn't exits, Got it? AHAHAHA!
      subForum.deletePost(unExistedPost) must be_==(Throw(SubForumException("Posts didn't exist or already deleted.")))

    }

    "Failure for duplicated deletion post" in new Ctx {
      val existedPost = "Tyrion" // Cause he died in the first book, So he doesn't exits, Got it? AHAHAHA!
      subForum.deletePost(existedPost)
      subForum.deletePost(existedPost) must be_==(Throw(SubForumException("Posts didn't exist or already deleted.")))

    }

  }


}
