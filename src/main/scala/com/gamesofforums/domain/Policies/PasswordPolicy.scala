package com.gamesofforums.domain.policies

trait PasswordPolicy {
  val policyType: String

  def isValid(password: String): Boolean

  def getPolicyType = policyType
}

object PasswordPolicy {

  def getPasswordPolicy(s: String): PasswordPolicy = {
    s match {
      case "weak" => new WeakPasswordPolicy()
      case "medium" => new MediumPasswordPolicy()
      case "best" => new HardPasswordPolicy()
    }
  }

  private class HardPasswordPolicy() extends PasswordPolicy {
    val policyType = "best"

    override def isValid(password: String): Boolean = password.length > 8
  }

  private class MediumPasswordPolicy() extends PasswordPolicy {
    val policyType = "medium"

    override def isValid(password: String): Boolean = password.length > 6
  }

  private class WeakPasswordPolicy() extends PasswordPolicy {
    val policyType = "weak"

    override def isValid(password: String): Boolean = password.length > 0
  }

}


