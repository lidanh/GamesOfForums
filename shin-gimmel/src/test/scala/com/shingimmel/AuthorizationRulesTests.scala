package com.shingimmel

import com.shingimmel.ShinGimmelMatchers
import org.specs2.mutable.Specification
import com.shingimmel.dsl._

/**
 * Created by lidanh on 4/24/15.
 */
class AuthorizationRulesTests extends Specification with ShinGimmelMatchers {

  /* Permissions */
  object Create extends Permission
  object Edit extends Permission
  object Delete extends Permission

  /* Fake resources */
  case class Post(content: String)
  case class User(name: String)

  val fakeUser = User("shingimmel")
  val fakePost = Post("hakshev!")

  "basic permission rules" should {
    val rut = rules {
      can(Create)
      can(Edit)
    }

    val someResource = Post("some post")

    "allows the given permissions but nothing else" in {
      rut must onlyHavePermissionsTo(Create, Edit)
    }

    "allows the given permissions for any kind of resource" in {
      rut.isDefinedAt(Create, someResource) must beTrue
      rut.isDefinedAt(Edit, someResource) must beTrue
    }
  }

  "restrict access to a specific kind of resource" should {
    val rut = rules {
      can(Create).a[Post]
    }

    "allows access to the restricted resource" in {
      rut.isDefinedAt(Create, fakePost) must beTrue
    }

    "block the given permission on other resources" in {
      rut.isDefinedAt(Create, fakeUser) must beFalse
    }
  }

  "restrict access based on a predicate" should {
    val rut = rules {
      can(Delete) onlyWhen { u: User => u.name == "shingimmel" }
    }

    "allows access to the resource who satisfies the predicate" in {
      rut.isDefinedAt(Delete, User("shingimmel")) must beTrue
    }

    "block access if predicate was not satisfied" in {
      rut.isDefinedAt(Delete, User("lidan")) must beFalse
    }
  }

  "rules derivation from single parent" should {
    val parent = rules {
      can(Create)
    }

    val rut = rules {
      derivedFrom(parent)
      can(Delete)
    }

    "allows access to parent's permissions and child's permissions but nothing else" in {
      rut must onlyHavePermissionsTo(Create, Delete)
    }

    "allows access to any kind of resource based on child's & parent's permissions" in {
      rut.isDefinedAt(Create, fakePost) must beTrue
      rut.isDefinedAt(Delete, fakeUser) must beTrue
    }
  }

  "rules derivation from multiple parents" should {
    val creation = rules { can(Create) }
    val deletion = rules { can(Delete) }

    val rut = rules {
      derivedFrom(creation)
      derivedFrom(deletion)

      can(Edit)
    }

    "allows access to all parents' permissions and child's permissions but nothing else" in {
      rut must onlyHavePermissionsTo(Create, Delete, Edit)
    }
  }

  "rules overridden of same kind" should {
    val parent = rules {
      can(Create) onlyWhen { u: User => u.name == "lidan" }
    }

    val rut = rules {
      derivedFrom(parent)
      can(Create) onlyWhen { u: User => u.name == "shingimmel" }
    }

    "give precedence to child's permissions" in {
      val user = User("lidan")
      parent.isDefinedAt(Create, user) must beTrue
      rut.isDefinedAt(Create, user) must beFalse
    }
  }

  "reduce permissions by rules overridden of different kind" should {
    val parent = rules {
      can(Create)
    }

    val rut = rules {
      derivedFrom(parent)
      can(Create).a[Post]
    }

    // Todo: Fix, it's not an expected behaviour!
    "combines parent's & child permissions" in {
      parent.isDefinedAt(Create, fakeUser) must beTrue
      rut.isDefinedAt(Create, fakeUser) must beTrue
    }
  }

  "extend permissions by rules overridden of different kinds" should {
    val parent = rules {
      can(Create).a[Post]
    }

    val rut = rules {
      derivedFrom(parent)
      can(Create)
    }

    "combines parent's & child permissions" in {
      parent.isDefinedAt(Create, fakeUser) must beFalse
      rut.isDefinedAt(Create, fakeUser) must beTrue
    }
  }
}
