package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/25/15.
 */
class ReportTests extends Specification with ResultMatchers {
  val user = User("bibi", "buzi", "bibi@buzi.com", "1234")
  val moderator = User("bugi", "kuki", "bugi@kuki.com", "0000", Moderator)
  val validReport = Report(user, moderator, "some report")

  "Report" should {
    "be valid with non-empty report text and a moderator" in {
      validReport.validate should succeed
    }

    "be invalid without report content" in {
      validReport.copy(content = "").validate should failWith("content" -> "must not be empty")
    }

    "be invalid if the user the report about is not a moderator" in {
      validReport.copy(moderator = moderator.copy(role = NormalUser)).validate should fail
    }
  }
}
