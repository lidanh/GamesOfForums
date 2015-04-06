package com.gamesofforums.domain

import com.gamesofforums.domain.Policies.ForumPolicy
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 05/04/2015.
 */
class ForumCreationTests extends Specification {
  "Create new sub-forum" should {
    trait Ctx extends Scope {
      val forum = new Forum("Base", ForumPolicy());
    }

    "Success for valid forum" in new Ctx {
      val forumName = "ValidForumName";
      val policy = ForumPolicy()

      forum.invalidInput(forumName,policy) must beFalse
    }

    "Failure for invalid forum" in new Ctx {
      val forumName = ""
      val policy = ForumPolicy()

      forum.invalidInput(forumName,policy) must beTrue
    }

    "Failure for invalid policy" in new Ctx {
      val forumName = "ValidForumName";
      val policy = null;

      forum.invalidInput(forumName,policy) must beTrue
    }
  }
}
