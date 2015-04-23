package com.gamesofforums.matchers

import com.twitter.util.{Throw, Try}
import org.specs2.matcher._

import scala.reflect.ClassTag


/**
 * Created by lidanh on 4/12/15.
 */
case class SuccessMatcher[T]() extends OptionLikeMatcher[Try, T, T]("a Success", (_: Try[T]).toOption)
case class FailureMatcher[T]() extends OptionLikeMatcher[Try, T, Throwable](
  "a Failure",
  (_:Try[T]) match {
    case Throw(e) => Some(e)
    case _ => None
  })

trait TwitterTryMatchers { this: Matchers =>
  def beSuccessful[T](result: Matcher[T] = AlwaysMatcher()): Matcher[Try[T]] = {
    SuccessMatcher[T]() and result ^^ { (_:Try[T]).get() }
  }

  def beSuccessful[T](result: T): Matcher[Try[T]] = {
    beSuccessful[T](===(result))
  }

  def beFailure[O, T <: Throwable : ClassTag](msg: Matcher[String] = AlwaysMatcher()): Matcher[Try[O]] = {
    // be a failure
    FailureMatcher[O]() and
    // be an instance of T
    beAnInstanceOf[T] ^^ { (_:Try[O]) match {
      case Throw(e) => e
      case _ => null
    } } and
    // message should be equal to the given msg
    new Matcher[Try[O]] {
      override def apply[S <: Try[O]](expectable: Expectable[S]): MatchResult[S] = {
        expectable.value match {
          case Throw(e) => createExpectable(e.getMessage).applyMatcher(msg).asInstanceOf[MatchResult[S]]
          case _ => failure("Expected a failure, but was successful", expectable)
        }
      }
    }
  }

  def beFailure[O, T <: Throwable : ClassTag](msg: String): Matcher[Try[O]] = {
    beFailure[O, T](===(msg))
  }
}
