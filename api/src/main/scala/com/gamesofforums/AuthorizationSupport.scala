package com.gamesofforums

import com.gamesofforums.domain.User
import com.gamesofforums.exceptions.AuthorizationException
import com.shingimmel.dsl.Permission

/**
 * Created by lidanh on 4/24/15.
 */
trait AuthorizationSupport {
  def withPermission(permission: Permission, resource: Any)(f: => Unit)(implicit user: User): Unit = {
    if (user.role.authRules.isDefinedAt(permission, resource)(user)) {
      f
    } else {
      throw new AuthorizationException(user, permission, resource)
    }
  }
}
