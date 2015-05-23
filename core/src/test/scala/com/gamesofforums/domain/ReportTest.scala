package com.gamesofforums.domain

import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/25/15.
 */
class ReportTest extends Specification with ResultMatchers {
  val user = User(
    generateId,
    firstName = "bibi",
    lastName = "buzi",
    mail = "bibi@buzi.com",
    password = "1234")
  val subforum = SubForum(generateId, name = "test")
  val otherUser = User(
    generateId,
    firstName = "bugi",
    lastName = "kuki",
    mail = "bugi@kuki.com",
    password = "0000",
    _role = Moderator(at = subforum))
  val validReport = Report(
    generateId,
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
