package com.gamesofforums.domain.integration

import com.gamesofforums.domain._
import com.gamesofforums.matchers.{RolesSharedTests, ForumMatchers}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/19/15.
 */
class RolePermissionsTests extends Specification with ForumMatchers with RolesSharedTests {

  val aNormalUser = NormalUser()
  val aModerator = Moderator()

  "a normal user" >> {
    behaveLike(aNormalUser)

    "have publish, edit and delete permissions but nothing else" in {
      aNormalUser must havePermissionOnlyTo(Publish, EditMessages, DeleteMessages)
    }
  }
  
  "a moderator" >> {
    behaveLike(aModerator)

    "have publish, edit, delete and ban users but nothing else" in {
      aModerator must havePermissionOnlyTo(Publish, EditMessages, DeleteMessages, Ban)
    }
  }
}