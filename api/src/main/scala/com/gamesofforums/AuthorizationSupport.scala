package com.gamesofforums

import com.gamesofforums.domain.User
import com.gamesofforums.exceptions.{AuthorizationException, UserSessionExpiredException}
import com.shingimmel.dsl.Permission

/**
 * Created by lidanh on 4/24/15.
 */
trait AuthorizationSupport {
  def withPermission[T](permission: Permission, resource: Any)(f: => T)(implicit user: User): T = {
    if (user.role.authRules.isDefinedAt(permission, resource)(user)) {
      f
    } else {
      throw new AuthorizationException(user, permission, resource)
    }
  }

  def onlyLoggedIn[T](f: => T)(implicit user: User): T = {
    if (user == null) throw new UserSessionExpiredException()

    f
  }
}
