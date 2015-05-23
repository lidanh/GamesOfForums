package com.gamesofforums.dbschema

import com.gamesofforums.matchers.ForumMatchers
import com.gamesofforums.{InMemoryStorage, ForumStorage}
import org.specs2.mutable.Specification
import com.gamesofforums.domain._

/**
 * Created by lidanh on 5/22/15.
 */
abstract class StorageTest extends Specification with ForumMatchers {
  def storage: ForumStorage

  val db = storage

  "add subforum" should {
    "store the given subforum" in {
      val subforum = SubForum(name = "test subforum")

      db.addSubforum(subforum)
      db.getSubforum(subforum.id) must beSome(subForumWith(name = subforum.name))
    }
  }

  "delete subforum" should {
    "remove the subforum from the database" in {
      val subforum = SubForum(name = "some subforum")

      db.addSubforum(subforum)
      db.deleteSubforum(subforum.id)

      db.getSubforum(subforum.id) must beNone
    }
  }

  "add user" should {
    "store the given user" in {
      val user = User(
        firstName = "kuki",
        lastName = "buki",
        mail = "some@mail.com",
        password = "1234"
      )

      db.addUser(user)
      db.getUser(user.mail) must beSome(
        userWith(
          mail = ===(user.mail),
          password = ===(user.password),
          firstname = ===(user.firstName),
          lastname = ===(user.lastName),
          role = ===(user.role)
        )
      )
    }

//    "store the subforums moderated by the user" in {
//      val subforum = SubForum(name = "some forum")
//      val user = User(
//        firstName = "kuki",
//        lastName = "buki",
//        mail = "some@mail.com",
//        password = "1234"
//      )
//      user is Moderator(at = subforum)
//
//      db.addSubforum(subforum)
//      db.addUser(user)
//    }
  }
}

class InMemoryTest extends StorageTest {
  override def storage: ForumStorage = new InMemoryStorage
}

class SlickStorageTest extends StorageTest {
  sequential

  override def storage: ForumStorage = new SlickStorage
}
