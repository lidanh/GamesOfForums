package com.gamesofforums

import java.util.UUID

import com.gamesofforums.domain._
import com.gamesofforums.exceptions.DataValidationImplicits._
import com.gamesofforums.exceptions._
import com.twitter.util.Try
import com.typesafe.scalalogging.LazyLogging
import com.wix.accord.{Failure, Success}

/**
 * Created by lidanh on 4/5/15.
 */
class ForumService(forum: Forum,   // todo: remove forum
                   db: InMemoryStorage,
                   passwordHasher: PasswordHasher = SHA1Hash,
                   mailService: MailService) extends AuthorizationSupport with LazyLogging {

  def getValue[T](obj: Option[T], id: Id): T = {
    obj match {
      case Some(t) => t
      case _ => throw new ObjectNotFoundException(id)
    }
  }

  def register(firstName: String, lastName: String, mail: String, password: String): Try[User] = {
    Try {
      // check duplication
      if (db.users.exists(_.mail == mail)) throw RegistrationException("User already registered")

      val user = User(
        firstName = firstName,
        lastName = lastName,
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
      withPermission(ManageSubForums) { user =>
        val subForum = SubForum(id = generateId, name = name)
        db.users.filter(u => moderators.contains(u.mail)).foreach(u => u is Moderator(subForum))

        subForum.validate(forum.policy) match {
          case Success => {
            val newSubforum = db.subforums += subForum
            logger.info(s"Subforum ${subForum} has created successfully.")
            subForum
          }
          case Failure(violations) => throw new InvalidDataException(violations)
        }
      }
    }
  }

  def publishPost(subForumId: Id, subject: String, content: String)(implicit user: Option[User] = None): Try[Post] = {
    Try {
      val subForum = getValue(db.subforums.find(_.id == subForumId), subForumId)

      withPermission(Publish) { loggedInUser =>

        val post = Post(
          id = generateId,
          subject = subject,
          content = content,
          postedBy = loggedInUser,
          postedIn = subForum)

        post.validate match {
          case Success => {
            db.messages += post

            post.subscribers += loggedInUser
            logger.info(s"${loggedInUser.mail} published a new post: ${post}")

            post
          }
          case Failure(violations) => throw new InvalidDataException(violations)
        }
      }
    }
  }

  def publishComment(parentMessageId: Id, content: String)(implicit user: Option[User] = None): Try[Comment] = {
    Try {
      withPermission(Publish) { loggedInUser =>
        val parent = getValue(db.messages.find(_.id == parentMessageId), parentMessageId)

        val comment = Comment(
          id = generateId,
          content = content,
          parent = parent,
          postedBy = loggedInUser)

        comment.validate match {
          case Success => {
            db.messages += comment
            val rootPost = comment.rootPost
            // subscribe user
            rootPost.subscribers += loggedInUser
            // notify post subscribers
            rootPost.subscribers.foreach(subscriber => if (subscriber != loggedInUser) subscriber.notify(comment))

            logger.info(s"${loggedInUser.mail} published a new comment: ${comment}")

            comment
          }
          case Failure(violations) => throw new InvalidDataException(violations)
        }
      }
    }
  }

  def report(subforumId: Id, moderatorId: Id, reportContent: String)(implicit user: Option[User] = None): Try[Report] = {
    Try {
      val subforum = getValue(db.subforums.find(_.id == subforumId), subforumId)
      val moderator = getValue(db.users.find(_.id == moderatorId), moderatorId)

      withPermission(ReportUsers) { reportedUser =>
        // validate that the reported user has already posted in the given subforum
        if (!subforum.messages.exists(m => m.postedBy == reportedUser))
          throw new ReportException("User hasn't publish a message the given subforum")

        // validate that the given moderator is a moderator in the given subforum
        if (!moderator.role.isInstanceOf[Moderator] || !subforum.moderators.contains(moderator.role))
          throw new ReportException("The given moderator is not a moderator in the given subforum")

        val report = Report(
          id = generateId,
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

  def deleteSubforum(subforumId: Id)(implicit user: Option[User] = None): Try[Unit] = {
    Try {
      withPermission(ManageSubForums) { user =>
        db.subforums.find(_.id == subforumId) match {
          case Some(s) => db.subforums -= s
          case _ => throw new SubForumException("subforum was not found")
        }
      }
    }
  }
}
