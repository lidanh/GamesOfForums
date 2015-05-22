package com.gamesofforums.exceptions

import com.gamesofforums.domain.IdType

/**
 * Created by lidanh on 5/10/15.
 */
case class ObjectNotFoundException(objectId: IdType) extends RuntimeException(s"$objectId was not found.")
