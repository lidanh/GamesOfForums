package com.gamesofforums.domain.integration

import com.gamesofforums.domain._
import com.gamesofforums.matchers.ForumMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by lidanh on 4/19/15.
 */
class PermissionsTests extends Specification with ForumMatchers {

  val aNormalUser = NormalUser()

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

  trait NormalUserCtx extends Ctx {
    val userPost = Post("some subject", "hakshev!", user)
    user.messages += userPost

    val otherUserPost = Post("other subject", "hofshi!", otherUser)
    otherUser.messages += otherUserPost
  }

  "a normal user" can {
    "publish, edit his own messages, delete his own messages but nothing else" in new NormalUserCtx {
      aNormalUser must havePermissionOnlyTo(Publish, EditMessages, DeleteMessages)
    }

    "publish anything" in new NormalUserCtx {
      val somePost = Post("some subject", "hakshev!", user)

      aNormalUser must havePermissionTo(Publish)(somePost)
    }

    "edit only if he owns the message" in new NormalUserCtx {
      aNormalUser must havePermissionTo(EditMessages)(userPost)
      aNormalUser must not(havePermissionTo(EditMessages)(otherUserPost))
    }

    "delete only if he owns the message" in new NormalUserCtx {
      aNormalUser must havePermissionTo(DeleteMessages)(userPost)
      aNormalUser must not(havePermissionTo(DeleteMessages)(otherUserPost))
    }
  }
}
