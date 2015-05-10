package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/25/15.
 */
class ReportTests extends Specification with ResultMatchers {
  val user = User(
    firstName = "bibi",
    lastName = "buzi",
    mail = "bibi@buzi.com",
    password = "1234")
  val subforum = SubForum(name = "test")
  val otherUser = User(
    firstName = "bugi",
    lastName = "kuki",
    mail = "bugi@kuki.com",
    password = "0000",
    _role = Moderator(at = subforum))
  val validReport = Report(
    reportedUser = user,
    otherUser = otherUser,
    content = "some report")

  "Report" should {
    "be valid with non-empty report content" in {
      validReport.validate should succeed
    }

    "be invalid without report content" in {
      validReport.copy(content = "").validate should failWith("content" -> "must not be empty")
    }
  }
}
