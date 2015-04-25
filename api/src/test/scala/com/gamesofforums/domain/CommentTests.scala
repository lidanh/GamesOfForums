package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/18/15.
 */
class CommentTests extends Specification with ResultMatchers {
  trait CommentCtx extends Scope {
    val fakeUser = User("fakename", "fakename", "some@mail.com", "pass")
    val subforum = SubForum("some forum", Seq(fakeUser))
    val post = Post("some subject", "some content", fakeUser, subforum)
    val validComment = Comment(
      content = "some content",
      parent = post,
      postedBy = fakeUser)
  }

  "Comment" should {
    "be valid with content" in new CommentCtx {
      validComment.validate should succeed
    }

    "be invalid without content" in new CommentCtx {
      validComment.copy(content = "").validate should failWith("content" -> "must not be empty")
    }

    "add itself to the parent's comments" in new CommentCtx {
      post.comments must contain(validComment)
    }

    "add itself to the root post subforum's messages" in new CommentCtx {
      subforum.messages must contain(post.asInstanceOf[Message]) and
        contain(validComment.asInstanceOf[Message])
    }
  }

  "Comment.root" should {
    "return the root post of the comment" in new CommentCtx {
      val comment = Comment(
        content = "level 3",
        postedBy = fakeUser,
        parent = Comment(
          content = "level 2",
          postedBy = fakeUser,
          parent = post
        )
      )

      comment.rootPost must be_==(post)
    }
  }

  "Delete comment comments" should {
    "remove all the comment's comments successfully" in new CommentCtx {
      val level1 = Comment("level1", validComment, fakeUser)
      val level2 = Comment("level2", level1, fakeUser)

      validComment.removeComments()

      validComment.comments must not(contain(level1))
      level1.comments must not(contain(level2))
      level2.comments must beEmpty
    }

    "remove all the comment's comments from the root post subforum's messages" in new CommentCtx {
      val level1 = Comment("level1", validComment, fakeUser)
      val level2 = Comment("level2", level1, fakeUser)

      validComment.removeComments()

      subforum.messages must not(contain(level1, level2))
    }
  }
}
