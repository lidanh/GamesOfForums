package com.shingimmel.dsl

import scala.language.experimental.macros

/**
 * Base trait for permission type
 *
 * @author Lidan Hifi
 * @since 23/04/15
 */
trait Permission

/**
 * Group of authorization rules
 *
 * @param permissions permissions
 * @tparam U permissions scope object type (e.g. User)
 *
 * @author Lidan Hifi
 * @since 23/04/15
 */
abstract case class AuthorizationRules[U](permissions: Set[Permission] = Set.empty[Permission]) {
  /**
   * Check if someone has permission to a given resource
   *
   * @param permission permission
   * @param resource resource
   * @return true if has permission, false if not
   */
  def isDefinedAt(permission: Permission, resource: Any)(implicit scope: U): Boolean
}

/**
 * A single authorization rule
 *
 * @param permission Rule permission
 * @param auth Access restriction function
 * @tparam U permissions scope object type (e.g. User)
 * @tparam R Resource type
 *
 * @author Lidan Hifi
 * @since 23/04/15
 */
case class AuthorizationRule[U <: Any, R <: Any](permission: Permission, auth: (U, R) => Boolean = (_: Any, _: Any) => true) {
  /**
   * Restrict access to resource based on a predicate
   *
   * @param predicate a resource => boolean function which defines whether someone has access to the resource or not
   * @tparam U permissions scope object type (e.g. User)
   * @tparam R Resource type
   * @return A new AuthorizationRule object
   */
  def onlyWhen[U, R](predicate: (U, R) => Boolean) = {
    copy(auth = predicate)
  }

  /**
   * Restrict access to a specific kind of resource
   * @tparam R Resource type
   * @return A new AuthorizationRule object
   */
  def a[R] = copy(auth = (_: Any, _: R) => true)

  /**
   * alias to [[AuthorizationRule.a]]
   * @tparam R
   * @return
   */
  def an[R] = a[R]
}
