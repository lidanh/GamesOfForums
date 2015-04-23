package com.shingimmel

import org.specs2.matcher.{Matcher, Matchers}

/**
 * Created by lidanh on 4/24/15.
 */
trait ShinGimmelMatchers { this: Matchers =>
  def onlyHavePermissionsTo(permissions: Permission*): Matcher[AuthorizationRules] = {
    beEqualTo(permissions.toSet) ^^ { (_: AuthorizationRules).permissions }
  }
}
