package com.gamesofforums.exceptions

/**
 * Created by lidanh on 4/5/15.
 */
case class InvalidDataException(invalidData: Set[DataViolation]) extends Exception("Invalid data")

case class DataViolation(constraint: String, description: String)

object DataValidationImplicits {
  import com.wix.accord.{Violation => AccordViolation}

  implicit def accordViolationToDataViolation(violations: Set[AccordViolation]): Set[DataViolation] = {
    violations.map(v =>
      DataViolation(if (v.constraint.startsWith("must match regular expression")) "must be valid" else v.constraint, v.description.getOrElse("undefined field")))
  }
}