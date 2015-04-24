package com.gamesofforums

import com.gamesofforums.domain._
import com.gamesofforums.exceptions.DataValidationImplicits._
import com.gamesofforums.exceptions.{InvalidDataException, LoginException, RegistrationException}
import com.twitter.util.Try
import com.wix.accord.{Failure, Success}

import scala.annotation.tailrec

/**
 * Created by lidanh on 4/5/15.
 */
class ForumService(forum: Forum, passwordHasher: PasswordHasher = SHA1Hash) {
  def register(firstName: String, lastName: String, mail: String, password: String): Try[User] = {
    val users = forum.users

    Try {
      // check duplication
      if (users.exists(_.mail == mail)) throw RegistrationException("User already registered")

      val user = User(firstName, lastName, mail, passwordHasher.hash(password))

      user.validate and forum.policy.passwordPolicy.validate(password) match {
        case Success => {
          users += user
          user
        }
        case Failure(violations) => throw new InvalidDataException(violations)
      }
    }
  }

  def login(mail: String, password: String): Try[User] = {
    Try {
      forum.users.find(_.mail == mail) match {
        case Some(user @ User(_, _, _, pass)) if pass == passwordHasher.hash(password) => user
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
      val post = Post(subject, content, postedBy)

      post.validate match {
        case Success => {
          subForum.posts += post
          postedBy.messages += post
          post.subscribers += postedBy
          post
        }
        case Failure(violations) => throw new InvalidDataException(violations)
      }
    }
  }

  @tailrec
  private def getRoot(message: Message): Post = {
    message match {
      case p: Post => p
      case c: Comment => getRoot(c.parent)
    }
  }

  def publishComment(parent: Message, content: String, postedBy: User): Try[Comment] = {
    Try {
      val comment = Comment(content, parent, postedBy)

      comment.validate match {
        case Success => {
          parent.comments += comment
          val rootPost = getRoot(comment)
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
}
