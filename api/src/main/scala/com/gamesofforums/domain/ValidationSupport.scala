package com.gamesofforums.domain

import com.wix.accord
import com.wix.accord.Validator

/**
 * Created by lidanh on 4/18/15.
 */
trait ValidationSupport {
  val validator: Validator[this.type]

  def validate = accord.validate[this.type](this)(validator)
}
