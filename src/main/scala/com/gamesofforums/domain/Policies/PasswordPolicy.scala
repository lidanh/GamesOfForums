package com.gamesofforums.domain.Policies

trait PasswordPolicy {
  def isValid(password: String): Boolean

  def getPolicyType(): String
}

object PasswordPolicy {

  def getPasswordPolicy(s: String): PasswordPolicy = {
    s match {
      case "weak" => new weakPasswordPolicy()
      case "medium" => new mediumPasswordPolicy()
      case "best" => new bestPasswordPolicy()
    }
  }

  private class bestPasswordPolicy() extends PasswordPolicy {
    val policyType = "best"

    override def isValid(password: String): Boolean = {
      if (password.length > 8) return true;
      false
    }

    override def getPolicyType(): String = policyType
  }

  private class mediumPasswordPolicy() extends PasswordPolicy {
    val policyType = "medium"

    override def isValid(password: String): Boolean = {
      if (password.length > 6) return true;
      false
    }

    override def getPolicyType(): String = policyType
  }

  private class weakPasswordPolicy() extends PasswordPolicy {
    val policyType = "weak"

    override def isValid(password: String): Boolean = {
      if (password.length > 0) return true;
      false
    }

    override def getPolicyType(): String = policyType
  }

}


