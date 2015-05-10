package com.gamesofforums.exceptions

import com.gamesofforums.domain.Id

/**
 * Created by lidanh on 5/10/15.
 */
case class ObjectNotFoundException(objectId: Id) extends RuntimeException(s"$objectId was not found.")
