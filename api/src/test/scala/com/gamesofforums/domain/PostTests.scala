package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 08/04/2015.
 */
class PostTests extends Specification with ResultMatchers {

  trait PostCtx extends Scope {
    val fakeUser = User("bibi", "buzi", "some@mail.com", "1234")
    val subforum = SubForum("some forum", Seq(fakeUser))
    val validPost = Post("some subject", "some content", fakeUser, subforum)
  }

  "Post" should {
    "be valid with subject and content" in new PostCtx {
      validPost.validate should succeed
    }

    "be invalid without subject" in new PostCtx {
      validPost.copy(subject = "").validate should failWith("subject" -> "must not be empty")
    }

    "be invalid without content" in new PostCtx {
      validPost.copy(content = "").validate should failWith("content" -> "must not be empty")
    }
  }

  "Post.root" should {
    "return the post itself" in new PostCtx {
      validPost.rootPost must be_==(validPost)
    }
  }

  "Delete post comments" should {
    "remove all the post's comments successfully" in new PostCtx {
      val level1 = Comment("level1", validPost, fakeUser)
      val level2 = Comment("level2", level1, fakeUser)

      validPost.removeComments()

      validPost.comments must not(contain(level1))
      level1.comments must not(contain(level2))
      level2.comments must beEmpty
    }

    "remove all the post's comments from the subforum messages" in new PostCtx {
      val level1 = Comment("level1", validPost, fakeUser)
      val level2 = Comment("level2", level1, fakeUser)

      validPost.removeComments()

      subforum.messages must not(contain(level1, level2))
    }
  }
}
