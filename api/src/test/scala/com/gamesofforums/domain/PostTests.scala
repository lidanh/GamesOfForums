package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 08/04/2015.
 */
class PostTests extends Specification with ResultMatchers {

  trait PostCtx extends Scope {
    val subforum = SubForum(name = "some forum")
    val fakeUser = User(
      firstName = "bibi",
      lastName = "buzi",
      mail = "some@mail.com",
      password = "1234",
      _role = Moderator(at = subforum))
    val validPost = Post(
      subject = "some subject",
      content = "some content",
      postedBy = fakeUser,
      postedIn = subforum)
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
      val level1 = Comment(content = "level1", parent = validPost, postedBy = fakeUser)
      val level2 = Comment(content = "level2", parent = level1, postedBy = fakeUser)

      validPost.removeComments()

      validPost.comments must not(contain(level1))
      level1.comments must not(contain(level2))
      level2.comments must beEmpty
    }

    "remove all the post's comments from the subforum messages" in new PostCtx {
      val level1 = Comment(content = "level1", parent = validPost, postedBy = fakeUser)
      val level2 = Comment(content = "level2", parent = level1, postedBy = fakeUser)

      validPost.removeComments()

      subforum.messages must not(contain(level1.asInstanceOf[Message])) and not(contain(level2.asInstanceOf[Message]))
    }
  }
}
