package com.gamesofforums

import com.sendgrid.SendGrid.{Response => SGResponse, Email}
import com.sendgrid._

import scala.concurrent._
import ExecutionContext.Implicits.global

/**
 * Created by lidanh on 4/25/15.
 */
class MailService {
  def sendMail(subject: String, recipients: Seq[String], content: String): Unit = {
    // todo: take credentials from properties file!
    val sendgrid = new SendGrid("gamesofforums", "GamesOfForums2014")

    val mail = new Email()
    mail.setFrom("noreply@games-of-forums.com")
    mail.setFromName("Games of Forums")
    mail.setSubject(subject)
    mail.setText(content)

    val mailResult = future {
      recipients.map { recipient =>
        mail.setTo(Array(recipient))

        sendgrid.send(mail)
      }
    } onSuccess {
      case responses: Seq[SGResponse] => responses.foreach(r => println(r.getMessage))
    }
  }
}

/* only for testing! */
object testapp extends App {
  val s = new MailService()
  s.sendMail("hello", Seq("euroil@gmail.com"), "test mail")

  Thread.sleep(1000000)
}
