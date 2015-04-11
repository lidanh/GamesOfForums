package com.gamesofforums.domain

import com.gamesofforums.domain.policies.ForumPolicy
import com.gamesofforums.exceptions.SubForumException
import com.twitter.util.{Return, Throw}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 05/04/2015.
 */
class SubForumCreationTests extends Specification{
  trait Ctx extends Scope {
    val forum = new Forum("Base", ForumPolicy())
  }

  "Create new sub-forum" should {
    "Success for valid sub-forum" in new Ctx {
      val subForumName = "subForumName"
      val moderators = List ("Woot", "What")

      forum.createNewSubforum(subForumName, moderators) must be_==(Return(subForumName))
    }
    "Failure for invalid forumName" in new Ctx {
      val subForumName = ""
      val moderators = List ("Woot", "What")

      forum.createNewSubforum(subForumName, moderators) must be_==(Throw(SubForumException("Invalid input: Creating subforum.")))
    }

    "Failure for invalid morderators" in new Ctx {
      val subForumName = "subForumName"
      val moderators = List ()

      forum.createNewSubforum(subForumName, moderators) must be_==(Throw(SubForumException("Invalid input: Creating subforum.")))
    }

    "Failure for duplicate forums names" in new Ctx {
      val subForumName = "subForumName"
      val moderators1 = List ("Neo") // He's the one.
      val moderators2 = List ("Agent Smith1", "Agent Smith2") // ... And so on.

      forum.createNewSubforum(subForumName, moderators1)
      forum.createNewSubforum(subForumName, moderators2) must be_==(Throw(SubForumException("Duplicate forum names")))
    }


  }

}
