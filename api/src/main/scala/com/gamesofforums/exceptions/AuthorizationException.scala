package com.gamesofforums.exceptions

import com.gamesofforums.domain.User
import com.shingimmel.dsl.Permission

/**
 * Created by lidanh on 4/19/15.
 */
class AuthorizationException(user: User, action: Permission, resource: Any) extends Exception("No permission") {
  s"${user.mail} does not have permission to ${action} in ${resource.toString}"
}
