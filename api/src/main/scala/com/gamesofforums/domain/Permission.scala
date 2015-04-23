package com.gamesofforums.domain

import com.shingimmel.Permission

/**
 * Created by lidanh on 4/19/15.
 */

/* Normal User Permissions */
object Publish extends Permission

object EditMessages extends Permission
object DeleteMessages extends Permission

/* Moderator Permissions */
object BanUsers extends Permission

/* Administrator Permissions */
object ManageSubForumModerators extends Permission
object ManageForumAdmins extends Permission
object ManageSubForums extends Permission

/* Admin */
object ManageForumPolicy extends Permission
