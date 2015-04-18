package com.gamesofforums.domain

import org.specs2.mutable.Specification

/**
 * Created by lidanh on 4/18/15.
 */
class SHA1HashTests extends Specification {
  "SHA-1 Hasher" should {
    "return valid SHA-1 digest for 'blabla'" in {
      SHA1Hash.hash("blabla") must be_==("bb21158c733229347bd4e681891e213d94c685be")
    }

    "return valid SHA-1 digest for 'azzam-azzam'" in {
      SHA1Hash.hash("azzam-azzam") must be_==("7e7307fb23a21380802f412ba511ef0c3e930024")
    }
  }
}
