package com.gamesofforums

import com.sendgrid.SendGrid.{Email, Response => SGResponse}
import com.sendgrid._
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 * Created by lidanh on 4/25/15.
 */
class MailService {
  private[this] val conf = ConfigFactory.load()
  private[this] lazy val username = conf.getString("mail.sendgrid.username")
  private[this] lazy val password = conf.getString("mail.sendgrid.password")

  def sendMail(subject: String, recipients: Seq[String], content: String): Unit = {
    val sendgrid = new SendGrid(username, password)

    val mail = new Email()
    mail.setFrom(conf.getString("from.mail"))
    mail.setFromName(conf.getString("from.name"))
    mail.setSubject(subject)
    mail.setText(content)

    future {
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
//  val s = new MailService()
//  s.sendMail("hello", Seq("euroil@gmail.com"), "test mail")
//
//  Thread.sleep(1000000)

}
