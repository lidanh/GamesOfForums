package com.gamesofforums.dbschema

import com.gamesofforums.domain._
import slick.driver.H2Driver.api._

/**
 * Created by lidanh on 5/22/15.
 */
object RoleMapper {
  val roles = Map(
    1 -> NormalUser,
    2 -> Moderator(Seq.empty),
    3 -> ForumAdmin,
    4 -> God
  )

  val codes = roles.map(_.swap)

  implicit val roleMapper = MappedColumnType.base[Role, Int](codes(_), roles(_))
}

class Users(tag: Tag) extends Table[User](tag, "users") with IdColumn {
  import RoleMapper._

  def mail = column[String]("mail")
  def password = column[String]("password")
  def firstname = column[String]("firstname")
  def lastname = column[String]("lastname")
  def role = column[Role]("role")
  def verificationCode = column[Option[String]]("verification_code")

  def * = (id, firstname, lastname, mail, password, role, verificationCode) <> ((User.apply _).tupled, User.unapply)
}

object Users extends TableQuery(new Users(_)) {
  val findById = this.findBy(_.id)
  val findByMail = this.findBy(_.mail)
}