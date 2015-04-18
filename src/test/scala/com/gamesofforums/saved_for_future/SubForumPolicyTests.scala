//package com.gamesofforums.olddomain
//
//import com.gamesofforums.domain.nottested.{ForumPolicyException, SubForumPolicy}
//import com.twitter.util.Throw
//import org.specs2.mutable.Specification
//
///**
// * Created by Guy Gonen on 06/04/2015.
// */
//class SubForumPolicyTests extends Specification {
//
//  "SubForum Policy" should {
//    "Success for valid SubForumPolicy" in {
//      val max = 3
//      val min = 1
//      val subForumPolicy = SubForumPolicy(min, max)
//      subForumPolicy.getMaxModerators() must beEqualTo(max)
//      subForumPolicy.getMinModerators() must beEqualTo(min)
//    }
//    "Failure for setting new min higher than max" in {
//      val max = 3
//      val min = 1
//      val subForumPolicy = SubForumPolicy(min, max)
//      subForumPolicy.setMinModerators(max + 1) must be_==(Throw(ForumPolicyException("New min lower than current max")))
//    }
//
//    "Failure for setting new max lower than min" in {
//      val max = 3
//      val min = 2
//      val subForumPolicy = SubForumPolicy(min, max)
//      subForumPolicy.setMaxModerators(min - 1) must be_==(Throw(ForumPolicyException("New max lower than current min")))
//    }
//
//  }
//
//}
