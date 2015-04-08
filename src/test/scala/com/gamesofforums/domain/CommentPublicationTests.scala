package com.gamesofforums.domain

import com.gamesofforums.exceptions.CommentException
import com.twitter.util.{Return, Throw}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 08/04/2015.
 */
class CommentPublicationTests extends Specification {

  trait Ctx extends Scope {
    val post = Post("someTitle", "someContent")
  }

  "Comment publication" should {
    "Success for valid input" in new Ctx {
      var commentContent = "someContentForComment"

      post.newCommentOnPost(commentContent) must be_==(Return(commentContent))
    }

    "Failure for invalid input" in new Ctx {
      var commentContent = ""

      post.newCommentOnPost(commentContent) must be_==(Throw(CommentException("Invalid comment.")))
    }
  }


}
