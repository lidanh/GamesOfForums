package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by Guy Gonen on 08/04/2015.
 */
class PostTests extends Specification with ResultMatchers {
  val fakeUser = User("bibi", "buzi", "some@mail.com", "1234")
  val validPost = Post("some subject", "some content", fakeUser)

  "Post" should {
    "be valid with subject and content" in {
      validPost.validate should succeed
    }

    "be invalid without subject" in {
      validPost.copy(subject = "").validate should failWith("subject" -> "must not be empty")
    }

    "be invalid without content" in {
      validPost.copy(content = "").validate should failWith("content" -> "must not be empty")
    }
  }
}
