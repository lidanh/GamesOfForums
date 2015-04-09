package com.gamesofforums.domain

import com.gamesofforums.exceptions.PostException
import com.twitter.util.{Return, Throw}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 08/04/2015.
 */
class PostPublicationTests extends Specification {

  trait Ctx extends Scope {
    val subForum = new SubForum("Base", List("Tyrion", "Tywin"))
  }

  "Topic Publication" should {
    "Success for valid input" in new Ctx {
      var topicTitle = "someTitle"
      var topicContent = "someContent"

      subForum.publishNewPost(topicTitle, topicContent) must be_==(Return(topicTitle))
    }

    "Failure for no topic" in new Ctx {
      var topicTitle = ""
      var topicContent = "someContent"

      subForum.publishNewPost(topicTitle, topicContent) must be_==(Throw(PostException("Invalid input.")))
    }

    "Failure for no content" in new Ctx {
      var topicTitle = "someTitle"
      var topicContent = ""

      subForum.publishNewPost(topicTitle, topicContent) must be_==(Throw(PostException("Invalid input.")))
    }

    "Failure for duplication of topics" in new Ctx {
      var topicTitle = "someTitle"
      var topicContent = "someContent"
      var topicOtherContent = "otherContent"

      subForum.publishNewPost(topicTitle, topicContent)
      subForum.publishNewPost(topicTitle, topicOtherContent) must be_==(Throw(PostException("Duplicated topic.")))
    }

    "Success for duplication of contents" in new Ctx {
      var topicTitle = "someTitle"
      var topicOtherTitle = "someOtherTitle"
      var topicContent = "someContent"

      subForum.publishNewPost(topicTitle, topicContent)
      subForum.publishNewPost(topicOtherTitle, topicContent) must be_==(Return(topicOtherTitle))
    }

  }


}
