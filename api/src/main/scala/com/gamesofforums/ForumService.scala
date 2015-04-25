package com.gamesofforums

import java.util.UUID

import com.gamesofforums.domain._
import com.gamesofforums.exceptions.DataValidationImplicits._
import com.gamesofforums.exceptions._
import com.twitter.util.Try
import com.wix.accord.{Failure, Success}

/**
 * Created by lidanh on 4/5/15.
 */
class ForumService(forum: Forum,
                   passwordHasher: PasswordHasher = SHA1Hash,
                    mailService: MailService = new MailService()) {

  def register(firstName: String, lastName: String, mail: String, password: String): Try[User] = {
    Try {
      // check duplication
      if (forum.users.exists(_.mail == mail)) throw RegistrationException("User already registered")

      val user = User(
        firstName,
        lastName,
        mail = mail,
        password = passwordHasher.hash(password),
        role = NormalUser,
        verificationCode = Some(UUID.randomUUID().toString))

      user.validate and forum.policy.passwordPolicy.validate(password) match {
        case Success => {
          forum.users += user
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
      forum.users.find(_.mail == mail) match {
        case Some(user) if user.password == passwordHasher.hash(password) => user
        case Some(_) => throw LoginException("Incorrect password")
        case None => throw LoginException("User is not registered")
      }
    }
  }

  def createSubforum(name: String, moderators: Seq[String]): Try[SubForum] = {
    Try {
      val moderatorsAsUsers = forum.users.filter(u => moderators.contains(u.mail))
      val subForum = SubForum(name, moderatorsAsUsers)

      subForum.validate(forum.policy) match {
        case Success => {
          forum.subForums += subForum
          subForum
        }
        case Failure(violations) => throw new InvalidDataException(violations)
      }
    }
  }

  def publishPost(subForum: SubForum, subject: String, content: String, postedBy: User): Try[Post] = {
    Try {
      val post = Post(subject, content, postedBy, subForum)

      post.validate match {
        case Success => {
          subForum.messages += post
          postedBy.messages += post
          post.subscribers += postedBy
          post
        }
        case Failure(violations) => throw new InvalidDataException(violations)
      }
    }
  }

  def publishComment(parent: Message, content: String, postedBy: User): Try[Comment] = {
    Try {
      val comment = Comment(content, parent, postedBy)

      comment.validate match {
        case Success => {
          parent.comments += comment
          val rootPost = comment.rootPost
          // subscribe user
          rootPost.subscribers += postedBy
          // notify post subscribers
          rootPost.subscribers.foreach(subscriber => if (subscriber != postedBy) subscriber.notify(comment))

          comment
        }
        case Failure(violations) => throw new InvalidDataException(violations)
      }
    }
  }

  def report(subforum: SubForum, reportedUser: User, moderator: User, reportContent: String): Try[Report] = {
    Try {
      // validate that the reported user has already posted in the given subforum
      if (!subforum.messages.exists(m => m.postedBy == reportedUser))
        throw new ReportException("User haven't publish a message the given subforum")

      // validate that the given moderator is a moderator in the given subforum
      if (!subforum.moderators.contains(moderator))
        throw new ReportException("The given moderator is not a moderator in the given subforum")

      val report = Report(
        reportedUser = reportedUser,
        moderator = moderator,
        content = reportContent)

      report.validate match {
        case Success => {
          forum.reports += report
          report
        }
        case Failure(violations) => throw new InvalidDataException(violations)
      }
    }
  }

  def deleteSubforum(subforum: SubForum): Try[Unit] = {
    Try {
      if (forum.subForums.contains(subforum)) {
        forum.subForums -= subforum
      } else {
        throw new SubForumException("subforum was not found")
      }
    }
  }
}
