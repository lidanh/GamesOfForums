package com.gamesofforums.domain

import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/25/15.
 */
class RolesTest extends Specification {
  "A moderator" should {
    "be stronger than a normal user" in {
      Moderator(Seq.empty).asInstanceOf[Role] must be > NormalUser
    }
  }

  "A forum admin" should {
    "be stronger than a moderator" in {
      ForumAdmin.asInstanceOf[Role] must be > Moderator(Seq.empty)
    }
  }

  "God" should {
    "be stronger than a forum admin" in {
      God.asInstanceOf[Role] must be > ForumAdmin
    }
  }

  "Transitivity roles" should {
    "success" in {
      God.asInstanceOf[Role] must be > NormalUser
    }
  }
}