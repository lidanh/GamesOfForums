package com.gamesofforums.domain

import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/25/15.
 */
class ModeratorTests extends Specification {
  "A moderator" should {
    "add itself to the subforum's moderators upon initialization" in {
      val user = User(
        generateId,
        firstName = "kuki",
        lastName = "buki",
        mail = "test@mail.com",
        password = "1234")
      val subforum = SubForum(generateId, name = "some forum")

      user is Moderator(at = subforum)

      subforum.moderators must be_==(Seq(user.role))
    }
  }
}
