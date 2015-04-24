package com.shingimmel

import scala.language.experimental.macros

/**
 * Library functions
 *
 * @author Lidan Hifi
 * @since 23/04/15
 */
package object dsl {
  /**
   * Define a new authorization rule
   *
   * @param permission permission
   * @return a new AuthorizationRule object, represents the rule
   */
  def can(permission: Permission) = AuthorizationRule(permission)

  /**
   * Derive permissions from other rules and extend the current rules set
   *
   * @param authorizationRules other authorization rules object
   */
  def derivedFrom[U](authorizationRules: AuthorizationRules[U]*) = { /* only for macro type safety */ }

  /**
   * Define authorization rules set
   *
   * @param authorizationRules rules definitions:
   *                           @usecase can(PERMISSION) defines access to all kinds of resources.
   *                                    @example can(Edit)
   *                           @usecase can(PERMISSION).a[RESOURCE] defines access to a specific type of resources
   *                                    @example can(Edit).a[Post]
   *                           @usecase can(PERMISSION) onlyWhen [T => Boolean] defines access to a resource based on a predicate function.
   *                                    @example can(Edit) onlyWhen { post: Post => post.content == "hello shingimel" }
   * @tparam U permissions scope object type (e.g. User)
   * @return
   */
  def rulesFor[U](authorizationRules: Any): AuthorizationRules[U] = macro RulesMacro.apply[U]
}
