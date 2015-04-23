package com.gamesofforums.matchers

import com.gamesofforums.domain.Role
import com.gamesofforums.exceptions.{InvalidDataException, DataViolation}
import com.shingimmel.{ShinGimmelMatchers, Permission}
import com.twitter.util.{Throw, Try}
import org.specs2.matcher.{MatchResult, Expectable, AlwaysMatcher, Matcher}
import org.specs2.matcher._

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

  def havePermissionTo(permissions: Permission*): Matcher[Role] = {
    onlyHavePermissionsTo(permissions: _*) ^^ { r: Role => r.authRules }
  }
}
