package com.gamesofforums.matchers

import com.gamesofforums.domain._
import com.gamesofforums.exceptions.{UserSessionExpiredException, AuthorizationException, DataViolation, InvalidDataException}
import com.shingimmel.ShinGimmelMatchers
import com.shingimmel.dsl.Permission
import com.twitter.util.{Throw, Try}
import org.specs2.matcher.{AlwaysMatcher, Expectable, MatchResult, Matcher, _}

import scala.collection.mutable.ListBuffer

/**
 * Created by lidanh on 4/19/15.
 */
trait ForumMatchers extends TwitterTryMatchers with ShinGimmelMatchers { this: Matchers =>
  private def violation(violation: (String, String)): Matcher[DataViolation] = {
    beEqualTo(violation._1) ^^ { (_: DataViolation).description } and
      startWith(violation._2) ^^ { (_: DataViolation).constraint }
  }

  def withViolation(field: (String, String)): Matcher[Set[DataViolation]] = {
    contain(violation(field))
  }

  def beDataViolationFailure(withViolations: Matcher[Set[DataViolation]] = AlwaysMatcher()): Matcher[Try[Any]] = {
    beFailure[Any, InvalidDataException]("Invalid data") and
    // violations matcher
    new Matcher[Try[Any]] {
      override def apply[S <: Try[Any]](expectable: Expectable[S]): MatchResult[S] = {
        expectable.value match {
          case Throw(e: InvalidDataException) => createExpectable(e.invalidData).applyMatcher(withViolations).asInstanceOf[MatchResult[S]]
          case Throw(e) => failure("Expected an invalid data failure, but got other failure", expectable)
          case _ => failure("Expected a failure, but was successful", expectable)
        }
      }
    }
  }

  def beAnAuthorizationFailure[T](user: String): Matcher[Try[T]] = {
    beFailure[T, AuthorizationException](contain(user) and contain("does not have permission"))
  }

  def beSessionExpiredFailure = beFailure[Any, UserSessionExpiredException]("User session expired. please log in.")

  def havePermissionOnlyTo(permissions: Permission*): Matcher[Role] = {
    onlyHavePermissionsTo(permissions: _*) ^^ { (_: Role).authRules aka "user authorization rules" }
  }

  def havePermissionTo(permission: Permission)(resource: Any)(implicit user: User): Matcher[Role] = {
    havePermissionTo[User](permission)(resource)(user) ^^ { (_: Role).authRules aka "user authorization rules" }
  }

  def userWith(mail: Matcher[String] = AlwaysMatcher(),
               password: Matcher[String] = AlwaysMatcher(),
               firstname: Matcher[String] = AlwaysMatcher(),
               lastname: Matcher[String] = AlwaysMatcher(),
               role: Matcher[Role] = AlwaysMatcher()): Matcher[User] = {
    mail ^^ { (_: User).mail } and
    password ^^ { (_: User).password } and
    firstname ^^ { (_: User).firstName } and
    lastname ^^ { (_: User).lastName } and
    role ^^ { (_: User).role }
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
}
