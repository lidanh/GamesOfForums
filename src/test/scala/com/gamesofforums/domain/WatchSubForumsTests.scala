package com.gamesofforums.domain

import com.gamesofforums.domain.policies.ForumPolicy
import org.specs2.mutable.Specification

/**
 * Created by Guy Gonen on 06/04/2015.
 */
class WatchSubForumsTests extends Specification {

  "Watching SubForums" should {
    "Success for watching forums" in {
      val max = 3
      val min = 1
      var forum = new Forum("Game of Forums", new ForumPolicy)
      forum.createNewSubforum("Fans of Tyrion", List("Cersi", "Arya"))
      forum.watchSubForumsList() must beEqualTo(List(SubForum("Fans of Tyrion", List("Cersi", "Arya"))))
    }
  }

}
