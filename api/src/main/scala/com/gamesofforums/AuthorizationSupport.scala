package com.gamesofforums

import com.gamesofforums.domain.User
import com.gamesofforums.exceptions.{AuthorizationException, UserSessionExpiredException}
import com.shingimmel.dsl.Permission

/**
 * Created by lidanh on 4/24/15.
 */
trait AuthorizationSupport {
  def withPermission[T](permission: Permission, resource: Any = None)(f: => T)(implicit user: Option[User]): T = {
    user.fold(throw new UserSessionExpiredException()) { user =>
      if (user.role.authRules.isDefinedAt(permission, resource)(user)) {
        f
      } else {
        throw new AuthorizationException(user, permission, resource)
      }
    }
  }
}
