package com.gamesofforums.domain.integration

import com.gamesofforums.domain.{DeleteMessages, EditMessages, Publish, NormalUser}
import com.gamesofforums.matchers.ForumMatchers
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/19/15.
 */
class PermissionsTests extends Specification with ForumMatchers {

  "a normal user" can {
    "publish, edit his own messages, delete his own messages but nothing else" in {
      NormalUser() must havePermissionTo(Publish, EditMessages, DeleteMessages)
    }
  }
}
