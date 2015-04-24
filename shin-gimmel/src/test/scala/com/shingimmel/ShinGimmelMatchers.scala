package com.shingimmel

import com.shingimmel.dsl.{AuthorizationRules, Permission}
import org.specs2.matcher.{Matcher, Matchers}

/**
 * Created by lidanh on 4/24/15.
 */

trait ShinGimmelMatchers { this: Matchers =>
  def onlyHavePermissionsTo(permissions: Permission*): Matcher[AuthorizationRules[_]] = {
    beEqualTo(permissions.toSet) ^^ { (_: AuthorizationRules[_]).permissions }
  }

  def havePermissionTo[U](permission: Permission)(resource: Any): U => Matcher[AuthorizationRules[U]] = { implicit scope: U =>
    beTrue ^^ { (_: AuthorizationRules[U]).isDefinedAt(permission, resource) }
  }
}
