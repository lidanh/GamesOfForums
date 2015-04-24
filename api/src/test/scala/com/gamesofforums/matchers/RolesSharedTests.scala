package com.gamesofforums.matchers

import com.gamesofforums.domain._
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/24/15.
 */
trait RolesSharedTests { this: Specification with ForumMatchers =>
  trait Ctx extends Scope {
    implicit val user = User(
      firstName = "kuki",
      lastName = "buki",
      mail = "some@email.com",
      password = "somePass")

    val otherUser = User(
      firstName = "bibi",
      lastName = "buzi",
      mail = "bib@i.com",
      password = "1234"
    )
  }

  def behaveLike(role: Role) = {
    role match {
      case r: NormalUser => normalUserBehaviour(r)
      case r: Moderator => moderatorBehaviour(r)
      case r: ForumAdmin => forumAdminBehaviour(r)
      case r: God => forumGodBehaviour(r)
    }
  }

  private def normalUserBehaviour(role: Role) = {
    trait NormalUserCtx extends Ctx {
      val someForum = SubForum("test", Seq.empty)
      val userPost = Post("some subject", "hakshev!", user, someForum)
      user.messages += userPost

      val otherUserPost = Post("other subject", "hofshi!", otherUser, someForum)
      otherUser.messages += otherUserPost
    }

    "can publish anything" in new NormalUserCtx {
      val somePost = Post("some subject", "hakshev!", user, someForum)

      role must havePermissionTo(Publish)(somePost)
    }

    "can edit only messages he owns" in new NormalUserCtx {
      role must havePermissionTo(EditMessages)(userPost) and
        not(havePermissionTo(EditMessages)(otherUserPost))
    }

    "can delete only messages he owns" in new NormalUserCtx {
      role must havePermissionTo(DeleteMessages)(userPost) and
        not(havePermissionTo(DeleteMessages)(otherUserPost))
    }
  }

  private def moderatorBehaviour(role: Role) = {
    trait ModeratorCtx extends Ctx {
      val moderatedSubforum = SubForum("test forum", Seq(user))
      val postInModeratedSubforum = Post("hello", "test message", otherUser, moderatedSubforum)
      val commentInModeratedSubforum = Comment("some comment", postInModeratedSubforum, otherUser)

      val notModeratedSubforum = SubForum("other forum", Seq.empty)
      val otherPost = Post("hello", "test message", otherUser, notModeratedSubforum)
      val otherComment = Comment("some comment", otherPost, otherUser)
    }

    "behave like a normal user" in {
      normalUserBehaviour(role)
    }

    "can ban users" in new ModeratorCtx {
      role must havePermissionTo(Ban)(otherUser)
    }

    "can edit any post in forums that he moderates" in new ModeratorCtx {
      role must havePermissionTo(EditMessages)(postInModeratedSubforum) and
        not(havePermissionTo(EditMessages)(otherPost))
    }

    "can edit any comment in forums that he moderates" in new ModeratorCtx {
      role must havePermissionTo(EditMessages)(commentInModeratedSubforum) and
        not(havePermissionTo(EditMessages)(otherComment))
    }

    "can delete any post in forums that he moderates" in new ModeratorCtx {
      role must havePermissionTo(DeleteMessages)(postInModeratedSubforum) and
        not(havePermissionTo(DeleteMessages)(otherPost))
    }

    "can delete any comment forums that he moderates" in new ModeratorCtx {
      role must havePermissionTo(DeleteMessages)(commentInModeratedSubforum) and
        not(havePermissionTo(DeleteMessages)(otherComment))
    }
  }

  private def forumAdminBehaviour(role: Role) = {
    trait ForumAdminCtx extends Ctx {
      val forum = Forum(ForumPolicy())
    }

    "behave like a moderator" in {
      moderatorBehaviour(role)
    }

    "can manage subforums moderators" in new ForumAdminCtx {
      role must havePermissionTo(ManageSubForumModerators)(forum)
    }

    "can manage forum admins" in new ForumAdminCtx {
      role must havePermissionTo(ManageForumAdmins)(forum)
    }

    "can manage subforums" in new ForumAdminCtx {
      role must havePermissionTo(ManageSubForums)(forum)
    }
  }

  private def forumGodBehaviour(role: Role) = {
    trait GodCtx extends Ctx {
      val forum = Forum(ForumPolicy())
    }

    "behave like a forum admin" in {
      forumAdminBehaviour(role)
    }

    "can manage forum policy" in new GodCtx {
      role must havePermissionTo(ManageForumPolicy)(forum)
    }
  }
}
