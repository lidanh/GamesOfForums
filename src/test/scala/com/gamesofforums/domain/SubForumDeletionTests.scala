package com.gamesofforums.domain

import com.gamesofforums.domain.policies.ForumPolicy
import com.gamesofforums.exceptions.SubForumException
import com.twitter.util.{Return, Throw}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by Guy Gonen on 09/04/2015.
 */
class SubForumDeletionTests extends Specification {

  trait Ctx extends Scope {
    val forum = new Forum("Base", ForumPolicy())
    forum.createNewSubforum("Westeros", List("Tommen", "Cersei"))
    forum.createNewSubforum("The North", List("Jon Snow", "Stanis"))
    forum.createNewSubforum("Winterfall", List("Ned Stark", "Robb Stark"))
  }

  "Subforum deletion" should {
    "Success for valid subforum" in new Ctx {
      forum.deleteSubForum("Winterfall") must be_==(Return())
    }

    "Failure for not existed forum" in new Ctx {
      forum.deleteSubForum("someLand") must be_==(Throw(SubForumException("SubForum didn't exist or already deleted.")))
    }

    "Failure for duplicated deletion" in new Ctx {
      forum.deleteSubForum("Winterfall") must be_==(Return())
      forum.deleteSubForum("Winterfall") must be_==(Throw(SubForumException("SubForum didn't exist or already deleted.")))
    }

  }


}
