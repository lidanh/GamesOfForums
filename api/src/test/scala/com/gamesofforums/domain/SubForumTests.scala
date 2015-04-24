package com.gamesofforums.domain


import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/18/15.
 */
class SubForumTests extends Specification with ResultMatchers {
  val moderator = User("bibi", "buzi", "someone@gmail.com", "1234")
  val validSubforum = SubForum("some name", Seq(moderator))
  val minModerators = 1
  val maxModerators = 3
  val defaultPolicy = ForumPolicy(minModerators = minModerators, maxModerators = maxModerators)

  "Subforum" should {
    "be valid with name and at least one moderator" in {
      validSubforum.validate(defaultPolicy) should succeed
    }

    "be invalid without name" in {
      validSubforum.copy(name = "").validate(defaultPolicy) should
        failWith("name" -> "must not be empty")
    }

    "be invalid without moderators" in {
      validSubforum.copy(moderators = Seq.empty).validate(defaultPolicy) should
        failWith("moderators count" -> s"got 0, expected between $minModerators and $maxModerators")
    }

    "be invalid when moderators num doesn't meet the policy" in {
      val somePolicy = ForumPolicy(minModerators = 1, maxModerators = 1)
      val anotherModerator = moderator.copy(firstName = "azzam", lastName = "azzam", mail = "az@zam.com")

      validSubforum.copy(moderators = Seq(moderator, anotherModerator)).validate(somePolicy) should
        failWith("moderators count" -> "got 2, expected between 1 and 1")
    }
  }
}
