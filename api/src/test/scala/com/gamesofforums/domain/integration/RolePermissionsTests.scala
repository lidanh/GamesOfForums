package com.gamesofforums.domain.integration

import com.gamesofforums.domain._
import com.gamesofforums.matchers.{RolesSharedTests, ForumMatchers}
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/19/15.
 */
class RolePermissionsTests extends Specification with RolesSharedTests with ForumMatchers {

  val aNormalUser = NormalUser()
  val aModerator = Moderator()
  val aForumAdmin = ForumAdmin()

  "a normal user" >> {
    behaveLike(aNormalUser)

    "have publish, edit and delete permissions but nothing else" in {
      aNormalUser must havePermissionOnlyTo(Publish, EditMessages, DeleteMessages)
    }
  }
  
  "a moderator" >> {
    behaveLike(aModerator)

    "have publish, edit, delete and ban users but nothing else" in {
      aModerator must havePermissionOnlyTo(
        Publish,
        EditMessages,
        DeleteMessages,
        Ban)
    }
  }

  "a forum admin" >> {
    behaveLike(aForumAdmin)

    "have publish, edit, delete, ban users, manage subforums moderators, manage forum admins, but nothing else" in {
      aForumAdmin must havePermissionOnlyTo(
        Publish,
        EditMessages,
        DeleteMessages,
        Ban,
        ManageSubForumModerators,
        ManageForumAdmins,
        ManageSubForums)
    }
  }

  "god" >> {
    val god = God()

    behaveLike(god)

    "can do everything" in {
      god must havePermissionOnlyTo(
        Publish,
        EditMessages,
        DeleteMessages,
        Ban,
        ManageSubForumModerators,
        ManageForumAdmins,
        ManageSubForums,
        ManageForumPolicy)
    }
  }
}
