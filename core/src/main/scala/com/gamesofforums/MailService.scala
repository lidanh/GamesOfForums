package com.gamesofforums

/**
 * Created by lidanh on 4/27/15.
 */
trait MailService {
  def sendMail(subject: String, recipients: Seq[String], content: String): Unit
}
