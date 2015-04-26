package com.gamesofforums

import com.gamesofforums.domain.User
import com.gamesofforums.exceptions.{AuthorizationException, UserSessionExpiredException}
import com.shingimmel.dsl.Permission
import com.typesafe.scalalogging.LazyLogging

/**
 * Created by lidanh on 4/24/15.
 */
trait AuthorizationSupport { this: LazyLogging =>
  def withPermission[T](permission: Permission, resource: Any = None)(f: => T)(implicit user: Option[User]): T = {
    user.fold(throw new UserSessionExpiredException()) { user =>
      if (user.role.authRules.isDefinedAt(permission, resource)(user)) {
        logger.info(s"User ${user.mail} try to ${permission.getClass.getSimpleName}}")
        f
      } else {
        throw new AuthorizationException(user, permission, resource)
      }
    }
  }
}
