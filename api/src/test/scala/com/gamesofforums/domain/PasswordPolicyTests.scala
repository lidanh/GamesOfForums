package com.gamesofforums.domain

import com.gamesofforums.domain.PasswordPolicy.{HardPasswordPolicy, MediumPasswordPolicy, WeakPasswordPolicy}
import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by Guy Gonen on 06/04/2015.
 */
class PasswordPolicyTests extends Specification with ResultMatchers {

  "Weak password policy" should {
    val weakPolicy = WeakPasswordPolicy

    "return success upon a valid weak password" in {
      weakPolicy.validate("123") should succeed
    }

    "return failure upon an invalid weak password" in {
      weakPolicy.validate("") should failWith("password" -> "must not be empty")
    }
  }

  "Medium password policy" should {
    val mediumPolicy = MediumPasswordPolicy

    "return success upon a valid medium password" in {
      mediumPolicy.validate("123456") should succeed
    }

    "return failure upon an invalid medium password" in {
      mediumPolicy.validate("") should failWith("password" -> "has size 0, expected 6 or more")
    }
  }

  "Hard password policy" should {
    val hardPolicy = HardPasswordPolicy

    "return success upon a valid hard password" in {
      hardPolicy.validate("12345678") should succeed
    }

    "return failure upon an invalid hard password" in {
      hardPolicy.validate("") should failWith("password" -> "has size 0, expected 8 or more")
    }
  }
}
