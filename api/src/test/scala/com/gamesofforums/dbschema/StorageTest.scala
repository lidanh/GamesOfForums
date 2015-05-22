package com.gamesofforums.dbschema

import com.gamesofforums.dbschema.SlickStorage
import com.gamesofforums.matchers.ForumMatchers
import com.gamesofforums.{InMemoryStorage, ForumStorage}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import com.gamesofforums.domain._

/**
 * Created by lidanh on 5/22/15.
 */
abstract class StorageTest extends Specification with ForumMatchers {
  def storage: ForumStorage

  trait Ctx extends Scope {
    val db = storage
  }

  "add subforum" should {
    "save the subforum in database" in new Ctx {
      val subforum = SubForum(name = "test subforum")

      db.addSubforum(subforum)
      db.getSubforum(subforum.id) must beSome(subForumWith(name = subforum.name))
    }
  }

  "delete subforum" should {
    "remove the subforum from the database" in new Ctx {
      val subforum = SubForum(name = "some subforum")

      db.addSubforum(subforum)
      db.deleteSubforum(subforum.id)

      db.getSubforum(subforum.id) must beNone
    }
  }
}

class InMemoryTest extends StorageTest {
  override def storage: ForumStorage = new InMemoryStorage
}

class SlickStorageTest extends StorageTest {
  override def storage: ForumStorage = new SlickStorage
}
