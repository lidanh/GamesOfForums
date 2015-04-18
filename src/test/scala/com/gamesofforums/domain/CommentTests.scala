package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/18/15.
 */
class CommentTests extends Specification with ResultMatchers {
  val fakeUser = User("fakename", "fakename", "some@mail.com", "pass")
  val post = Post("some subject", "some content", fakeUser)
  val validComment = Comment(content = "some content", parent = post, postedBy = fakeUser)

  "Comment" should {
    "be valid with content" in {
      validComment.validate should succeed
    }

    "be invalid without content" in {
      validComment.copy(content = "").validate should failWith("content" -> "must not be empty")
    }
  }
}
