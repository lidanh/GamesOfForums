package com.gamesofforums

import java.util.UUID

import com.gamesofforums.domain._
import com.gamesofforums.exceptions.DataValidationImplicits._
import com.gamesofforums.exceptions._
import com.twitter.util.Try
import com.typesafe.scalalogging.{StrictLogging, LazyLogging}
import com.wix.accord.{Failure, Success}

/**
 * Created by lidanh on 4/5/15.
 */
class ForumService(forum: Forum,   // todo: remove forum
                   db: InMemoryStorage,
                   passwordHasher: PasswordHasher = SHA1Hash,
                   mailService: MailService) extends AuthorizationSupport with LazyLogging {

  def register(firstName: String, lastName: String, mail: String, password: String): Try[User] = {
    Try {
      // check duplication
      if (db.users.exists(_.mail == mail)) throw RegistrationException("User already registered")

      val user = User(
        firstName,
        lastName,
        mail = mail,
        password = passwordHasher.hash(password),
        _role = NormalUser,
        verificationCode = Some(UUID.randomUUID().toString))

      user.validate and forum.policy.passwordPolicy.validate(password) match {
        case Success => {
          db.users += user
          logger.info(s"User ${user.mail} has registered successfully.")

          // send verification mail
          mailService.sendMail("Verifiction", Seq(user.mail), s"Verify your account: ${user.verificationCode}")

          user
        }
        case Failure(violations) => throw new InvalidDataException(violations)
      }
    }
  }

  def login(mail: String, password: String): Try[User] = {
    Try {
      db.users.find(_.mail == mail) match {
        case Some(user) if user.password == passwordHasher.hash(password) => {
          logger.info(s"User ${user.mail} has logged in successfully.")
          user
        }
        case Some(_) => {
          logger.warn(s"User ${mail} tried to log in with incorrect password.")
          throw LoginException("Incorrect password")
        }
        case None => {
          logger.warn(s"User ${mail} tried to log in but he is not a registered user.")
          throw LoginException("User is not registered")
        }
      }
    }
  }

  def createSubforum(name: String, moderators: Seq[String])(implicit user: Option[User] = None): Try[SubForum] = {
    Try {
      withPermission(ManageSubForums) {
        val subForum = SubForum(name)
        db.users.filter(u => moderators.contains(u.mail)).foreach(u => u is Moderator(subForum))

        subForum.validate(forum.policy) match {
          case Success => {
            db.subforums += subForum
            logger.info(s"Subforum ${subForum} has created successfully.")
            subForum
          }
          case Failure(violations) => throw new InvalidDataException(violations)
        }
      }
    }
  }

  // todo: remove postedby because we already have it implicitly
  def publishPost(subForum: SubForum, subject: String, content: String, postedBy: User)(implicit user: Option[User] = None): Try[Post] = {
    Try {
      val post = Post(subject, content, postedBy, subForum)

      withPermission(Publish, post) {
        post.validate match {
          case Success => {
            db.messages += post

            post.subscribers += postedBy
            logger.info(s"${postedBy.mail} published a new post: ${post}")

            post
          }
          case Failure(violations) => throw new InvalidDataException(violations)
        }
      }
    }
  }

  def publishComment(parent: Message, content: String, postedBy: User)(implicit user: Option[User] = None): Try[Comment] = {
    Try {
      val comment = Comment(content, parent, postedBy)

      withPermission(Publish, comment) {
        comment.validate match {
          case Success => {
            parent.comments += comment
            val rootPost = comment.rootPost
            rootPost.postedIn.messages += comment
            // subscribe user
            rootPost.subscribers += postedBy
            // notify post subscribers
            rootPost.subscribers.foreach(subscriber => if (subscriber != postedBy) subscriber.notify(comment))

            logger.info(s"${postedBy.mail} published a new comment: ${comment}")

            comment
          }
          case Failure(violations) => throw new InvalidDataException(violations)
        }
      }
    }
  }

  def report(subforum: SubForum, reportedUser: User, moderator: User, reportContent: String)(implicit user: Option[User] = None): Try[Report] = {
    Try {
      withPermission(ReportUsers) {
        // validate that the reported user has already posted in the given subforum
        if (!subforum.messages.exists(m => m.postedBy == reportedUser))
          throw new ReportException("User hasn't publish a message the given subforum")

        // validate that the given moderator is a moderator in the given subforum
        if (!moderator.role.isInstanceOf[Moderator] || !subforum.moderators.contains(moderator.role))
          throw new ReportException("The given moderator is not a moderator in the given subforum")

        val report = Report(
          reportedUser = reportedUser,
          otherUser = moderator,
          content = reportContent)

        report.validate match {
          case Success => {
            db.reports += report
            report
          }
          case Failure(violations) => throw new InvalidDataException(violations)
        }
      }
    }
  }

  def deleteSubforum(subforum: SubForum)(implicit user: Option[User] = None): Try[Unit] = {
    Try {
      withPermission(ManageSubForums) {
        if (db.subforums.contains(subforum)) {
          db.subforums -= subforum
        } else {
          throw new SubForumException("subforum was not found")
        }
      }
    }
  }
}
