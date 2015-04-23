package com.gamesofforums.domain.integration

import com.gamesofforums.ForumService
import com.gamesofforums.domain.PasswordPolicy.WeakPasswordPolicy
import com.gamesofforums.domain._
import com.gamesofforums.exceptions.{LoginException, RegistrationException}
import com.gamesofforums.matchers.{ForumMatchers, TwitterTryMatchers}
import org.specs2.matcher.{AlwaysMatcher, Matcher}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by lidanh on 4/5/15.
 *
 */
class ForumServiceIT extends Specification with ForumMatchers with Mockito {
  val firstName = "bibi"
  val lastName = "zibi"
  val someEmail = "someEmail@someDomain.com"
  val somePass = "somePass"

  trait Ctx extends Scope {
    val forum = Forum(policy = ForumPolicy())
    val forumService = new ForumService(forum = forum)
  }

  def userWith(mail: Matcher[String] = AlwaysMatcher(),
                password: Matcher[String] = AlwaysMatcher()): Matcher[User] = {
    mail ^^ { (_: User).mail } and
    password ^^ { (_: User).password }
  }

  def postWith(subject: Matcher[String] = AlwaysMatcher(),
                content: Matcher[String] = AlwaysMatcher(),
                postedBy: Matcher[User] = AlwaysMatcher(),
                subscribers: Matcher[mutable.Set[User]] = AlwaysMatcher()) = {
    subject ^^ { (_: Post).subject } and
    content ^^ { (_: Post).content } and
    postedBy ^^ { (_: Post).postedBy } and
    subscribers ^^ { (_: Post).subscribers }
  }

  def subForumWith(name: String): Matcher[SubForum] = ===(name) ^^ { (_: SubForum).name aka "sub forum name" }

  def commentWith(content: String): Matcher[Comment] = ===(content) ^^ { (_: Comment).content aka "comment content" }

  "User registration" should {
    "success for a valid user and password" in new Ctx {
      val result = forumService.register(firstName, lastName, someEmail, somePass)
      result must beSuccessful(userWith(mail = ===(someEmail)))
    }

    "store the password as SHA-1 digest" in new Ctx {
      forumService.register(firstName, lastName, someEmail, "blabla") must
        beSuccessful(userWith(password = ===("bb21158c733229347bd4e681891e213d94c685be" /* SHA-1 digest of "blabla" */)))
    }

    "failed if user already registered" in new Ctx {
      val sameMail = "someEmail@someDomain.com"
      forumService.register("asdf", "xcv", sameMail, "somePass")

      forumService.register("asdasas", "dfbdfg", sameMail, "somePass") must beFailure[User, RegistrationException]("User already registered")
    }

    "failed for invalid details" in new Ctx {
      forumService.register(
        firstName = firstName,
        lastName = lastName,
        mail = "someEmail",
        password = somePass) must beDataViolationFailure(withViolation("mail" -> "must be valid"))
    }

    "failed for an invalid password (doesn't meet the current password policy)" in {
      val userManagerWithPolicy = new ForumService(forum = Forum(policy = ForumPolicy(WeakPasswordPolicy)))

      userManagerWithPolicy.register(
        firstName = firstName,
        lastName = lastName,
        mail = someEmail,
        password = "") must beDataViolationFailure(withViolation("password" -> "must not be empty"))
    }
  }

  "User login" should {
    "success for registered user and correct password" in new Ctx {
      forumService.register("blabla", "blabla", someEmail, somePass)

      forumService.login(someEmail, somePass) must beSuccessful(userWith(mail = ===(someEmail)))
    }

    "failed for registered user but incorrect password" in new Ctx {
      forumService.register("blabla", "blabla", someEmail, somePass)

      forumService.login(someEmail, "incorrectPass") must beFailure[User, LoginException]("Incorrect password")
    }

    "failed for unregistered user" in new Ctx {
      forumService.login(someEmail, somePass) must beFailure[User, LoginException]("User is not registered")
    }
  }

  "Create subforum" should {
    "success for a valid subforum" in new Ctx {
      val subforumName = "some name"
      forumService.register("blabla", "blabla", someEmail, "blabla")

      val result = forumService.createSubforum(name = subforumName, moderators = List(someEmail))
      result must beSuccessful(subForumWith(name = subforumName))
      forum.subForums must contain(result.get())
    }

    "failed for invalid subforum" in new Ctx {
      forumService.register("blabla", "blabla", someEmail, "blabla")

      forumService.createSubforum(name = "", moderators = List(someEmail)) must
        beDataViolationFailure(withViolation("name" -> "must not be empty"))
    }

    "failed when the subforum does not meet the forum policy" in new Ctx {
      val someForumWithNoModeratorsPolicy = Forum(policy = ForumPolicy(minModerators = 0, maxModerators = 0))
      val service = new ForumService(forum = someForumWithNoModeratorsPolicy)
      service.register("blabla", "blabla", someEmail, "blabla")

      service.createSubforum("some subforum", List(someEmail)) must beDataViolationFailure(withViolation("moderators count" -> "got 1, expected between 0 and 0"))
    }
  }

  class PublishCtx extends Ctx {
    val fakeUser = User("bla", "bla", "e@mail.com", "somepass")
    val fakeSubforum = SubForum("some name", Seq(fakeUser))
    val fakePost = Post("kaka", "kaka", fakeUser)
  }

  "Publish post" should {
    "success for a valid post, and the user who published is subscribed to the new post" in new PublishCtx {
      val someSubject = "helloworld"
      val someContent = "kukibuki"

      val result = forumService.publishPost(fakeSubforum, someSubject, someContent, fakeUser)
      result must beSuccessful(postWith(subject = ===(someSubject),
                              content = ===(someContent),
                              postedBy = ===(fakeUser),
                              subscribers = contain(fakeUser)))
      fakeSubforum.posts must contain(result.get())
    }

    "add the published post to the user's posts" in new PublishCtx {
      val post = forumService.publishPost(fakeSubforum, "bibi", "buzi", fakeUser).get()

      fakeUser.posts must contain(post)
    }

    "failed for an invalid post (no subject)" in new PublishCtx {
      forumService.publishPost(fakeSubforum, subject = "", "kukibuki", fakeUser) must
        beDataViolationFailure(withViolation("subject" -> "must not be empty"))
    }
  }

  "Publish comment" should {
    "success for a valid comment" in new PublishCtx {
      val someContent = "yo!"

      val result = forumService.publishComment(fakePost, someContent, fakeUser)
      result must beSuccessful(commentWith(someContent))
      fakePost.comments must contain(result.get())
      fakePost.subscribers must contain(fakeUser)
    }

    "notify post subscribers except the comment publisher" in new PublishCtx {
      val commentPublisher = mock[User]
      commentPublisher.posts returns ListBuffer[Post]()

      // add subscriber to post
      val somePost = Post("bibi", "zibi", commentPublisher)
      val otherSubscriber = mock[User]
      somePost.subscribers += otherSubscriber

      val comment = forumService.publishComment(somePost, "blabla", commentPublisher).get()

      there was one(otherSubscriber).notify(comment)
      there was no(commentPublisher).notify(comment)
    }

    "failed for an invalid comment" in new PublishCtx {
      forumService.publishComment(fakePost, "", fakeUser) must beDataViolationFailure(withViolation("content" -> "must not be empty"))
    }
  }
}
