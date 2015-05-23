package com.gamesofforums.domain.integration

import com.gamesofforums.dbschema.SlickStorage
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
import slick.driver.H2Driver.api._

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
    db.usersStorage += normalUser.get
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
      db.subforumsStorage must contain(result.get())
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

  trait PublishCtx extends Ctx {
    val fakeSubforum = SubForum(name = "some name")
    db.subforumsStorage += fakeSubforum

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

      db.messagesStorage must contain(post)
      fakeSubforum.messages must contain(post)
      normalUser.get.messages must contain(post)
    }

    "failed for an invalid post (no subject)" in new PublishCtx with NormalUser {
      forumService.publishPost(fakeSubforum.id, subject = "", "kukibuki") must
        beDataViolationFailure(withViolation("subject" -> "must not be empty"))
    }

    "failed for unknown subforum" in new PublishCtx with NormalUser {
      val unknownSubforum = SubForum(name = "unknown")
      forumService.publishPost(unknownSubforum.id, subject = "blabla", content = "blabl") must
        beFailure[Post, ObjectNotFoundException](contain(unknownSubforum.id))
    }

    "failed for guest user (doesn't have permission to publish)" in new PublishCtx {
      forumService.publishPost(fakeSubforum.id, subject = "", "kukibuki") must beSessionExpiredFailure
    }
  }

  trait PublishCommentCtx extends PublishCtx {
    val fakePost = Post(subject = "kaka", content = "kaka", postedBy = fakeUser, postedIn = fakeSubforum)
    db.messagesStorage += fakePost
  }

  "Publish comment" should {
    "success for a valid comment" in new PublishCommentCtx with NormalUser {
      val someContent = "yo!"

      val result = forumService.publishComment(fakePost.id, someContent)
      result must beSuccessful(commentWith(someContent))

      // test persistency
      db.messagesStorage must contain(result.get())
      fakePost.comments must contain(result.get())
      fakePost.subscribers must contain(normalUser.get)
      fakeSubforum.messages must contain(result.get())
    }

    "notify post subscribers except the comment publisher" in new PublishCommentCtx with NormalUser {
      val commentPublisher = mock[User]
      commentPublisher.messages returns ListBuffer[Message]()

      // add subscriber to post
      val somePost = Post(generateId, subject = "bibi", content = "zibi", postedBy = commentPublisher, postedIn = fakeSubforum)
      db.messagesStorage += somePost
      val otherSubscriber = mock[User]
      somePost.subscribers += otherSubscriber

      val comment = forumService.publishComment(somePost.id, "blabla").get()

      there was one(otherSubscriber).notify(comment)
      there was no(commentPublisher).notify(comment)
    }

    "failed for an invalid comment" in new PublishCommentCtx with NormalUser {
      forumService.publishComment(fakePost.id, "") must beDataViolationFailure(withViolation("content" -> "must not be empty"))
    }

    "failed for unknown post" in new PublishCommentCtx with NormalUser {
      val unknownPost = Post(subject = "blabla", content = "blabla", postedBy = normalUser.get, postedIn = fakeSubforum)

      forumService.publishComment(unknownPost.id, content = "blabla") must
        beFailure[Comment, ObjectNotFoundException](contain(unknownPost.id))
    }

    "failed for guest user (doesn't have permission to publish)" in new PublishCommentCtx {
      forumService.publishComment(fakePost.id, "") must beSessionExpiredFailure
    }
  }

  trait ReportCtx extends Ctx {
    val user = User(
      generateId,
      firstName = "some normal user",
      lastName = "blabla",
      mail = "test@user.com",
      password = "1234")
    db.usersStorage += user

    val subforum = SubForum(generateId, name = "some forum")
    db.subforumsStorage += subforum

    val moderator = User(
      generateId,
      firstName = "some moderator",
      lastName = "kuki",
      mail = "mod@erator.com",
      password = "0000",
      _role = Moderator(at = subforum))
    db.usersStorage += moderator
  }

  "report moderator" should {
    "success if the user was already published a post in the moderator's subforum" in new ReportCtx with NormalUser {
      forumService.publishPost(
        subforumId = subforum.id,
        subject = "test post",
        content = "bla bla"
      )

      val someContent = "some complaint"
      val result = forumService.report(subforum.id, moderator.id, someContent)

      result must beSuccessful(reportWith(someContent))
      db.reportsStorage must contain(result.get())
    }

    "success if the user was already published a comment in the moderator's subforum" in new ReportCtx with NormalUser {
      val parentpost = forumService.publishPost(
        subforumId = subforum.id,
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
        subforumId = subforum.id,
        subject = "test post",
        content = "bla bla"
      )

      forumService.report(subforum.id, moderator.id, "blabla") must beFailure[Report, ReportException]("The given moderator is not a moderator in the given subforum")
    }

    "failed if the given `moderator` is not a moderator" in new ReportCtx with NormalUser {
      val regularUser = moderator.copy(id = generateId, _role = NormalUser)
      db.usersStorage += regularUser

      forumService.publishPost(
        subforumId = subforum.id,
        subject = "test post",
        content = "bla bla"
      )

      forumService.report(subforum.id, regularUser.id, "blabla") must beFailure[Report, ReportException]("The given moderator is not a moderator in the given subforum")
    }

    "failed if the given subforum does not exist" in new ReportCtx with NormalUser {
      val unknownSubforum = SubForum(name = "unknown subforum")

      forumService.publishPost(
        subforumId = unknownSubforum.id,
        subject = "test post",
        content = "bla bla"
      )

      forumService.report(unknownSubforum.id, moderator.id, "blabla") must beFailure[Report, ObjectNotFoundException](contain(unknownSubforum.id))
    }

    "failed if the given moderator does not exist" in new ReportCtx with NormalUser {
      val unknownModerator = moderator.copy(id = generateId)

      forumService.report(subforum.id, unknownModerator.id, "blabla") must beFailure[Report, ObjectNotFoundException](contain(unknownModerator.id))
    }

    "failed for unauthorized user (doesn't have permission to report)" in new ReportCtx {
      forumService.report(subforum.id, moderator.id, "blabla") must beSessionExpiredFailure
    }
  }

  "subforum deletion" should {
    "success if the subforum exists" in new Ctx with ForumAdminUser {
      val moderatorMail = "some@moderator.com"
      db.usersStorage += User(
        generateId,
        firstName = "bibi",
        lastName = "bugi",
        mail = moderatorMail,
        password = "1234")
      val deletedSubforum = forumService.createSubforum("someLand", Seq(moderatorMail)).get()

      forumService.deleteSubforum(deletedSubforum.id) must beSuccessful[Unit]
      db.subforumsStorage must not(contain(deletedSubforum))
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
