package com.shingimmel

import com.shingimmel.dsl.AuthorizationRules

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Macro for creating authorization rules
 *
 * @param context
 * @param exprs
 * @tparam C
 *
 * @author Lidan Hifi
 * @since 23/04/15
 */
private class RulesMacro[C <: Context, U : C#WeakTypeTag](val context: C, exprs: C#Tree) {
  import context.universe._

  trait Pattern
  case class RulePattern(permission: context.Tree,
                         resource: Option[context.Tree] = None,
                         f: Option[context.Tree] = None) extends Pattern
  case class DerivedPattern(from: context.Tree) extends Pattern

  def extractPatterns(e: C#Tree): Seq[Pattern] = {
    e match {
      // can statement
      case q"com.shingimmel.dsl.`package`.can($permission)" =>
        Seq(RulePattern(permission))

      // can with resource type
      case q"com.shingimmel.dsl.`package`.can($permission).a[$tpe]" =>
        Seq(RulePattern(permission, Some(tq"$tpe")))
      case q"com.shingimmel.dsl.`package`.can($permission).an[$tpe]" =>
        Seq(RulePattern(permission, Some(tq"$tpe")))

      // can with onlyWhen statement
      case q"com.shingimmel.dsl.`package`.can($permission).onlyWhen[$utpe, $rtpe]($f)" =>
        Seq(RulePattern(permission, Some(tq"$rtpe"), Some(q"$f")))

      // derivation statement
      case q"com.shingimmel.dsl.`package`.derivedFrom[$tpe](..${parents: List[context.Tree]})" =>
        parents.map { case p: context.Tree => DerivedPattern(q"$p") }

      // block of rules
      case Block(rules) => e.children.flatMap { case expr: context.Tree =>
        extractPatterns(expr) }

      // invalid rule (compilation error)
      case expr: context.Tree =>
        context.abort(expr.pos, s"Invalid authorization rule: ${showCode(expr)}")
    }
  }

  def transform: context.Expr[AuthorizationRules[U]] = {
    val patterns = extractPatterns(exprs)

    val cases = patterns.collect {
      case RulePattern(permission, None, _) => cq"($permission, _) => true"
      case RulePattern(permission, Some(resourceType), None) => cq"($permission, r: $resourceType) => true"
      case RulePattern(permission, Some(resourceType), Some(f)) => cq"($permission, r: $resourceType) => $f(scope, r)"
    }

    val derivedCases = patterns.collect {
      case DerivedPattern(parent) => cq"_ if $parent.isDefinedAt(permission, resource)(scope) => true"
    }

    val permissions = patterns.collect { case r: RulePattern => r.permission }

    val permissionsSet = patterns.collect {
      case DerivedPattern(parent) => q"$parent.permissions"
    }.fold(q"Set(..$permissions)")( (acc, parent) => q"$acc ++ $parent" )

    val result = context.Expr[AuthorizationRules[U]] {
      q"""new AuthorizationRules[${weakTypeOf[U]}]($permissionsSet) {
          override def isDefinedAt(permission: Permission, resource: Any)(implicit scope: ${weakTypeOf[U]}): Boolean = {
            (permission, resource) match {
              case ..$cases
              case ..$derivedCases
              case _ => false
            }
          }
      }"""
    }

//    context.info(NoPosition, showCode(result.tree), true)

    result
  }
}

object RulesMacro {
  def apply[U : c.WeakTypeTag](c: Context)(authorizationRules: c.Tree): c.Expr[AuthorizationRules[U]] = new RulesMacro[c.type, U](c, authorizationRules).transform
}