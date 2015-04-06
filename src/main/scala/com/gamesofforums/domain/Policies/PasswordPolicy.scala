package com.gamesofforums.domain.Policies

trait PasswordPolicy {
  def isValid(password: String): Boolean
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

    override def isValid(password: String): Boolean = {
      if (password.length > 8) return true;
      false
    }
  }

  private class mediumPasswordPolicy() extends PasswordPolicy {
    override def isValid(password: String): Boolean = {
      if (password.length > 6) return true;
      false
    }
  }

  private class weakPasswordPolicy() extends PasswordPolicy {
    override def isValid(password: String): Boolean = {
      if (password.length > 0) return true;
      false
    }
  }

}


