package com.shingimmel

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
 *
 * @author Lidan Hifi
 * @since 23/04/15
 */
abstract case class AuthorizationRules(permissions: Set[Permission] = Set.empty[Permission]) {
  /**
   * Check if someone has permission to a given resource
   *
   * @param permission permission
   * @param resource resource
   * @return true if has permission, false if not
   */
  def isDefinedAt(permission: Permission, resource: Any): Boolean
}

/**
 * A single authorization rule
 *
 * @param permission Rule permission
 * @param auth Access restriction function
 * @tparam T Resource type
 *
 * @author Lidan Hifi
 * @since 23/04/15
 */
case class AuthorizationRule[T <: Any](permission: Permission, auth: T => Boolean = (_: Any) => true) {
  /**
   * Restrict access to resource based on a predicate
   *
   * @param predicate a resource => boolean function which defines whether someone has access to the resource or not
   * @tparam T Resource type
   * @return A new AuthorizationRule object
   */
  def onlyWhen[T](predicate: T => Boolean) = {
    copy(auth = predicate)
  }

  /**
   * Restrict access to a specific kind of resource
   * @tparam U Resource type
   * @return A new AuthorizationRule object
   */
  def a[U] = copy(auth = (_: U) => true)

  /**
   * @see a
   * @tparam U
   * @return
   */
  def an[U] = a[U]
}
