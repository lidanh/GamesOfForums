package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/18/15.
 */
class CommentTests extends Specification with ResultMatchers {
  trait CommentCtx extends Scope {
    val fakeUser = User(
      firstName = "fakename",
      lastName = "fakename",
      mail = "some@mail.com",
      password = "pass")
    val subforum = SubForum(name = "some forum")
    val post = Post(
      subject = "some subject",
      content = "some content",
      postedBy = fakeUser,
      postedIn = subforum)
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
      val level1 = Comment(
        content = "level1",
        parent = validComment,
        postedBy = fakeUser)
      val level2 = Comment(
        content = "level2",
        parent = level1,
        postedBy = fakeUser)

      validComment.removeComments()

      validComment.comments must not(contain(level1))
      level1.comments must not(contain(level2))
      level2.comments must beEmpty
    }

    "remove all the comment's comments from the root post subforum's messages" in new CommentCtx {
      val level1 = Comment(
        content = "level1",
        parent = validComment,
        postedBy = fakeUser)
      val level2 = Comment(
        content = "level2",
        parent = level1,
        postedBy = fakeUser)

      validComment.removeComments()

      subforum.messages must not(contain(level1, level2))
    }
  }
}
