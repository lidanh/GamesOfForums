package com.gamesofforums.domain.integration

import com.gamesofforums.domain.PasswordPolicy.WeakPasswordPolicy
import com.gamesofforums.domain._
import com.gamesofforums.exceptions._
import com.gamesofforums.matchers.ForumMatchers
import com.gamesofforums.{ForumService, InMemoryStorage, MailService}
import org.mockito.Matchers
import org.specs2.matcher.{AlwaysMatcher, Matcher}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

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

  def userWith(mail: Matcher[String] = AlwaysMatcher(),
               password: Matcher[String] = AlwaysMatcher()): Matcher[User] = {
    mail ^^ {
      (_: User).mail
    } and
      password ^^ {
        (_: User).password
      }
  }

  def postWith(subject: Matcher[String] = AlwaysMatcher(),
               content: Matcher[String] = AlwaysMatcher(),
               postedBy: Matcher[User] = AlwaysMatcher(),
               subscribers: Matcher[ListBuffer[User]] = AlwaysMatcher()) = {
    subject ^^ {
      (_: Post).subject
    } and
      content ^^ {
        (_: Post).content
      } and
      postedBy ^^ {
        (_: Post).postedBy
      } and
      subscribers ^^ {
        (_: Post).subscribers
      }
  }

  def subForumWith(name: String): Matcher[SubForum] = ===(name) ^^ { (_: SubForum).name aka "sub forum name" }

  def commentWith(content: String): Matcher[Comment] = ===(content) ^^ { (_: Comment).content aka "comment content" }

  def reportWith(content: String): Matcher[Report] = ===(content) ^^ { (_: Report).content aka "report content" }

  trait Ctx extends Scope {
    val mailService = new MailService {
      // simple mail service, just to test the contract
      override def sendMail(subject: String, recipients: Seq[String], content: String): Unit = {
        println(s"mail sent to $recipients: $subject / $content")
      }
    }

    val db = new InMemoryStorage
    val forum = Forum(policy = ForumPolicy())
    val forumService = new ForumService(forum = forum, db = db, mailService = mailService)
  }

  trait ForumAdminUser extends Scope {
    val userMail = "ad@min.com"
    implicit val adminUser = Some(User(
      generateId,
      firstName = "some admin",
      lastName = "some admin",
      mail = userMail,
      password = "****",
      _role = ForumAdmin))
  }

  trait NormalUser extends Ctx {
    val userMail = "u@ser.com"
    implicit val normalUser = Some(User(
      firstName = "some user",
      lastName = "some user",
      mail = userMail,
      password = "$$",
      _role = NormalUser))
    db.users += normalUser.get
  }

  "Forum initialization" should {
    "forum is working???" in {
      "yes" must be_===("yes")
    }

    "grade must be 100" in {
      val grade = 100

      grade must beEqualTo(100)
    }
  }

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

    "failed for an invalid password (doesn't meet the current password policy)" in new Ctx {
      val userManagerWithPolicy = new ForumService(
        forum = Forum(policy = ForumPolicy(WeakPasswordPolicy)),
        db = db,
        mailService = mailService)

      userManagerWithPolicy.register(
        firstName = firstName,
        lastName = lastName,
        mail = someEmail,
        password = "") must beDataViolationFailure(withViolation("password" -> "must not be empty"))
    }

    "send verification code to user's email upon registration" in new Ctx {
      val mockMailService = mock[MailService]
      val mockedService = new ForumService(
        forum = forum,
        db = db,
        mailService = mockMailService)

      val result = mockedService.register(firstName, lastName, someEmail, somePass)

      val verificationCode = result.get().verificationCode.getOrElse("unknown")

      there was one(mockMailService).sendMail(
        subject = anyString,
        recipients = Matchers.eq(Seq(someEmail)),
        content = contain(verificationCode))
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

  "User logout" should {
    pending("because it's an API, so we don't have a session for the logged-in user. will be added with the web layer")
  }

  "Create subforum" should {
    "success for a valid subforum" in new Ctx with ForumAdminUser {
      val subforumName = "some name"
      forumService.register("blabla", "blabla", someEmail, "blabla")

      val result = forumService.createSubforum(name = subforumName, moderators = List(someEmail))

      result must beSuccessful(subForumWith(name = subforumName))
      db.subforums must contain(result.get())
    }

    "failed for invalid subforum" in new Ctx with ForumAdminUser {
      forumService.register("blabla", "blabla", someEmail, "blabla")

      forumService.createSubforum(name = "", moderators = List(someEmail)) must
        beDataViolationFailure(withViolation("name" -> "must not be empty"))
    }

    "failed when the subforum does not meet the forum policy" in new Ctx with ForumAdminUser {
      val someForumWithNoModeratorsPolicy = Forum(policy = ForumPolicy(minModerators = 0, maxModerators = 0))
      val service = new ForumService(
        forum = someForumWithNoModeratorsPolicy,
        db = db,
        mailService = mailService)
      service.register("blabla", "blabla", someEmail, "blabla")

      service.createSubforum("some subforum", List(someEmail)) must beDataViolationFailure(withViolation("moderators count" -> "got 1, expected between 0 and 0"))
    }

    "failed for unauthorized user (doesnt have permission to create subforum)" in new Ctx with NormalUser {
      forumService.createSubforum("some subforum", List("no@permission.com")) must beAnAuthorizationFailure[SubForum](userMail)
    }
  }

  trait ReportCtx extends Ctx {
    val user = User(
      generateId,
      firstName = "some normal user",
      lastName = "blabla",
      mail = "test@user.com",
      password = "1234")
    db.users += user

    val subforum = SubForum(generateId, name = "some forum")
    db.subforums += subforum

    val moderator = User(
      generateId,
      firstName = "some moderator",
      lastName = "kuki",
      mail = "mod@erator.com",
      password = "0000",
      _role = Moderator(at = subforum))
    db.users += moderator
  }

  class PublishCtx extends Ctx {
    val fakeSubforum = SubForum(name = "some name")
    db.subforums += fakeSubforum

    val fakeUser = User(
      generateId,
      firstName = "bla",
      lastName = "bla",
      mail = "e@mail.com",
      password = "somepass",
      _role = Moderator(at = fakeSubforum))
  }

  "Publish post" should {
    "success for a valid post, and the user who published is subscribed to the new post" in new PublishCtx with NormalUser {
      val someSubject = "helloworld"
      val someContent = "kukibuki"

      val result = forumService.publishPost(fakeSubforum.id, someSubject, someContent)
      result must beSuccessful(postWith(subject = ===(someSubject),
        content = ===(someContent),
        postedBy = ===(normalUser.get),
        subscribers = contain(normalUser.get)))
    }

    "persist the published post in db, user messages and subforum messages" in new PublishCtx with NormalUser {
      val post = forumService.publishPost(fakeSubforum.id, "bibi", "buzi").get()

      db.messages must contain(post)
      fakeSubforum.messages must contain(post)
      normalUser.get.messages must contain(post)
    }

    "failed for an invalid post (no subject)" in new PublishCtx with NormalUser {
      forumService.publishPost(fakeSubforum.id, subject = "", "kukibuki") must
        beDataViolationFailure(withViolation("subject" -> "must not be empty"))
    }

    "failed for guest user (doesn't have permission to publish)" in new PublishCtx {
      forumService.publishPost(fakeSubforum.id, subject = "", "kukibuki") must beSessionExpiredFailure
    }
  }

  "Publish comment" should {
    "success for a valid comment" in new PublishCommentCtx with NormalUser {
      val someContent = "yo!"

      val result = forumService.publishComment(fakePost.id, someContent)
      result must beSuccessful(commentWith(someContent))

      // test persistency
      db.messages must contain(result.get())
      fakePost.comments must contain(result.get())
      fakePost.subscribers must contain(normalUser.get)
      fakeSubforum.messages must contain(result.get())
    }

    "notify post subscribers except the comment publisher" in new PublishCommentCtx with NormalUser {
      val commentPublisher = mock[User]
      commentPublisher.messages returns ListBuffer[Message]()

      // add subscriber to post
      val somePost = Post(generateId, subject = "bibi", content = "zibi", postedBy = commentPublisher, postedIn = fakeSubforum)
      db.messages += somePost
      val otherSubscriber = mock[User]
      somePost.subscribers += otherSubscriber

      val comment = forumService.publishComment(somePost.id, "blabla").get()

      there was one(otherSubscriber).notify(comment)
      there was no(commentPublisher).notify(comment)
    }

    "failed for an invalid comment" in new PublishCommentCtx with NormalUser {
      forumService.publishComment(fakePost.id, "") must beDataViolationFailure(withViolation("content" -> "must not be empty"))
    }

    "failed for guest user (doesn't have permission to publish)" in new PublishCommentCtx {
      forumService.publishComment(fakePost.id, "") must beSessionExpiredFailure
    }
  }

  class PublishCommentCtx extends PublishCtx {
    val fakePost = Post(generateId, subject = "kaka", content = "kaka", postedBy = fakeUser, postedIn = fakeSubforum)
    db.messages += fakePost
  }

  "report moderator" should {
    "success if the user was already published a post in the moderator's subforum" in new ReportCtx with NormalUser {
      forumService.publishPost(
        subForumId = subforum.id,
        subject = "test post",
        content = "bla bla"
      )

      val someContent = "some complaint"
      val result = forumService.report(subforum.id, moderator.id, someContent)

      result must beSuccessful(reportWith(someContent))
      db.reports must contain(result.get())
    }

    "success if the user was already published a comment in the moderator's subforum" in new ReportCtx with NormalUser {
      val parentpost = forumService.publishPost(
        subForumId = subforum.id,
        subject = "test post",
        content = "bla bla"
      ).get()

      forumService.publishComment(
        parentMessageId = parentpost.id,
        content = "some comment"
      )

      val someContent = "some complaint"

      forumService.report(subforum.id, moderator.id, someContent) must beSuccessful(reportWith(someContent))
    }

    "failed if the user haven't post any message in the moderator's forum" in new ReportCtx with NormalUser {
      forumService.report(subforum.id, moderator.id, "some complaint") must beFailure[Report, ReportException]("User hasn't publish a message the given subforum")
    }

    "failed if the user the report about is not a moderator in the given subforum" in new ReportCtx with NormalUser {
      subforum._moderators.clear()

      forumService.publishPost(
        subForumId = subforum.id,
        subject = "test post",
        content = "bla bla"
      )

      forumService.report(subforum.id, moderator.id, "blabla") must beFailure[Report, ReportException]("The given moderator is not a moderator in the given subforum")
    }

    "failed if the given `moderator` is not a moderator" in new ReportCtx with NormalUser {
      val regularUser = moderator.copy(id = generateId, _role = NormalUser)
      db.users += regularUser

      forumService.publishPost(
        subForumId = subforum.id,
        subject = "test post",
        content = "bla bla"
      )

      forumService.report(subforum.id, regularUser.id, "blabla") must beFailure[Report, ReportException]("The given moderator is not a moderator in the given subforum")
    }

    "failed for unauthorized user (doesn't have permission to report)" in new ReportCtx {
      forumService.report(subforum.id, moderator.id, "blabla") must beSessionExpiredFailure
    }
  }

  "subforum deletion" should {
    "success if the subforum exists" in new Ctx with ForumAdminUser {
      val moderatorMail = "some@moderator.com"
      db.users += User(
        generateId,
        firstName = "bibi",
        lastName = "bugi",
        mail = moderatorMail,
        password = "1234")
      val deletedSubforum = forumService.createSubforum("someLand", Seq(moderatorMail)).get()

      forumService.deleteSubforum(deletedSubforum.id) must beSuccessful[Unit]
      db.subforums must not(contain(deletedSubforum))
    }

    "failed if the subforum doesnt exist" in new Ctx with ForumAdminUser {
      forumService.deleteSubforum(SubForum(name = "Winterfall").id) must beFailure[Unit, SubForumException]("subforum was not found")
    }

    "failed for unauthorized user (doesn't have permission to delete subforum)" in new Ctx with NormalUser {
      forumService.deleteSubforum(SubForum(name = "Winterfall").id) must beAnAuthorizationFailure[Unit](userMail)
    }
  }

  "message deletion" should {
    pending("because we don't have persistence layer.")
  }

  "user types" should {
    pending("because the requirements are unclear.")
  }
}
