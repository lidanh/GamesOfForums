package com.gamesofforums.domain


import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/18/15.
 */
class SubForumTests extends Specification with ResultMatchers {
  trait Ctx extends Scope {
    val validSubforum = SubForum(generateId, name = "some name")
    val moderator = User(
      generateId,
      firstName = "bibi",
      lastName = "buzi",
      mail = "someone@gmail.com",
      password = "1234",
      _role = Moderator(at = validSubforum))
    val minModerators = 1
    val maxModerators = 3
    val defaultPolicy = ForumPolicy(minModerators = minModerators, maxModerators = maxModerators)
  }

  "Subforum" should {
    "be valid with name and at least one moderator" in new Ctx {
      validSubforum.validate(defaultPolicy) should succeed
    }

    "be invalid without name" in new Ctx {
      validSubforum.copy(name = "").validate(defaultPolicy) should
        failWith("name" -> "must not be empty")
    }

    "be invalid without moderators" in new Ctx {
      validSubforum._moderators.clear()

      validSubforum.validate(defaultPolicy) should
        failWith("moderators count" -> s"got 0, expected between $minModerators and $maxModerators")
    }

    "be invalid when moderators num doesn't meet the policy" in new Ctx {
      val somePolicy = ForumPolicy(minModerators = 1, maxModerators = 1)
      val anotherModerator = User(
        generateId,
        firstName = "azzam",
        lastName = "azzam",
        mail = "az@zam.com",
        password = "1234",
        _role = Moderator(at = validSubforum))

      validSubforum.validate(somePolicy) should
        failWith("moderators count" -> "got 2, expected between 1 and 1")
    }
  }
}
