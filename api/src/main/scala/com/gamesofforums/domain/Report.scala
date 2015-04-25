package com.gamesofforums.domain

import com.wix.accord.Validator
import com.wix.accord.dsl.{validator => accordValidator, _}

/**
 * Created by lidanh on 4/25/15.
 */
case class Report(reportedUser: User, moderator: User, content: String) extends ValidationSupport {
  override val validator: Validator[Report] = Report.validator
}

object Report {
  implicit val validator = accordValidator[Report] { report =>
    report.content is notEmpty
    report.moderator.role is equalTo(Moderator) as "moderator"
  }
}
