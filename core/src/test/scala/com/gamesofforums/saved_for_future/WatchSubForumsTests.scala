//package com.gamesofforums.olddomain
//
//import com.gamesofforums.domain.User
//import com.gamesofforums.domain.nottested.ForumPolicy
//import org.specs2.mutable.Specification
//
///**
// * Created by Guy Gonen on 06/04/2015.
// */
//class WatchSubForumsTests extends Specification {
//
//  "Watching SubForums" should {
//    "Success for watching forums" in {
//      val max = 3
//      val min = 1
//      val admin = User("bibi", "zibi", "buzi@bla.com", "somePass")
//      val forum = new OldForum("Game of Forums", admin, new ForumPolicy)
//      forum.addSubForum("Fans of Tyrion", List("Cersi", "Arya"))
//      forum.watchSubForumsList() must beEqualTo(List(OldSubForum("Fans of Tyrion", List("Cersi", "Arya"))))
//    }
//  }
//
//}
