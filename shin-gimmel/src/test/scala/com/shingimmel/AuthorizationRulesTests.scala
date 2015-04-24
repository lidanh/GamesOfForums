package com.shingimmel

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
  case class Forum(name: String)
  case class User(name: String)

  implicit val user = User("shingimmel")
  val fakeForum = Forum("matkal")
  val fakePost = Post("hakshev!")

  "basic permission rules" should {
    val rut = rulesFor[User] {
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
    val rut = rulesFor[User] {
      can(Create).a[Post]
    }

    "allows access to the restricted resource" in {
      rut.isDefinedAt(Create, fakePost) must beTrue
    }

    "block the given permission on other resources" in {
      rut.isDefinedAt(Create, fakeForum) must beFalse
    }
  }

  "restrict access based on a predicate" should {
    val rut = rulesFor[User] {
      can(Delete) onlyWhen { (u: User, post: Post) => post.content == "hakshev" }
    }

    "allows access to a resource that satisfies the predicate" in {
      rut.isDefinedAt(Delete, Post("hakshev")) must beTrue
    }

    "block access if predicate was not satisfied" in {
      rut.isDefinedAt(Delete, Post("Yo")) must beFalse
    }
  }

  "rules derivation from single parent" should {
    val parent = rulesFor[User] {
      can(Create)
    }

    val rut = rulesFor[User] {
      derivedFrom(parent)
      can(Delete)
    }

    "allows access to parent's permissions and child's permissions but nothing else" in {
      rut must onlyHavePermissionsTo(Create, Delete)
    }

    "allows access to any kind of resource based on child's & parent's permissions" in {
      rut.isDefinedAt(Create, fakePost) must beTrue
      rut.isDefinedAt(Delete, fakeForum) must beTrue
    }
  }

  "rules derivation from multiple parents" should {
    val creation = rulesFor[User] { can(Create) }
    val deletion = rulesFor[User] { can(Delete) }

    val rut = rulesFor[User] {
      derivedFrom(creation)
      derivedFrom(deletion)

      can(Edit)
    }

    "allows access to all parents' permissions and child's permissions but nothing else" in {
      rut must onlyHavePermissionsTo(Create, Delete, Edit)
    }
  }

  "rules overridden of same kind" should {
    val somePostContent = "Hakshev!"

    val parent = rulesFor[User] {
      can(Create) onlyWhen { (u: User, post: Post) => post.content == somePostContent }
    }

    val rut = rulesFor[User] {
      derivedFrom(parent)
      can(Create) onlyWhen { (u: User, post: Post) => post.content == "Amod dom!" }
    }

    "give precedence to child's permissions" in {
      val post = Post(somePostContent)
      parent.isDefinedAt(Create, post) must beTrue
      rut.isDefinedAt(Create, post)  must beFalse
    }
  }

  "reduce permissions by rules overridden of different kind" should {
    val parent = rulesFor[User] {
      can(Create)
    }

    val rut = rulesFor[User] {
      derivedFrom(parent)
      can(Create).a[Post]
    }

    // Todo: Fix, it's not an expected behaviour!
    "combines parent's & child permissions" in {
      parent.isDefinedAt(Create, fakePost) must beTrue
      rut.isDefinedAt(Create, fakePost) must beTrue
    }
  }

  "extend permissions by rules overridden of different kinds" should {
    val parent = rulesFor[User] {
      can(Create).a[Post]
    }

    val rut = rulesFor[User] {
      derivedFrom(parent)
      can(Create)
    }

    "combines parent's & child permissions" in {
      parent.isDefinedAt(Create, fakeForum) must beFalse
      rut.isDefinedAt(Create, fakeForum) must beTrue
    }
  }
}
