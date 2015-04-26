# ReportTests

### Report should
  + be valid with non-empty report content
  + be invalid without report content

Total for specification ReportTests
Finished in 199 ms
2 examples, 0 failure, 0 error

# SHA1HashTests

### SHA-1 Hasher should
  + return valid SHA-1 digest for 'blabla'
  + return valid SHA-1 digest for 'azzam-azzam'

Total for specification SHA1HashTests
Finished in 162 ms
2 examples, 0 failure, 0 error

# ModeratorTests

### A moderator should
  + add itself to the subforum's moderators upon initialization

Total for specification ModeratorTests
Finished in 162 ms
1 example, 0 failure, 0 error

# PasswordPolicyTests

### Weak password policy should
  + return success upon a valid weak password
  + return failure upon an invalid weak password
### Medium password policy should
  + return success upon a valid medium password
  + return failure upon an invalid medium password
### Hard password policy should
  + return success upon a valid hard password
  + return failure upon an invalid hard password

Total for specification PasswordPolicyTests
Finished in 196 ms
6 examples, 0 failure, 0 error

# PostTests

### Post should
  + be valid with subject and content
  + be invalid without subject
  + be invalid without content
### Post.root should
  + return the post itself
### Delete post comments should
  + remove all the post's comments successfully
  + remove all the post's comments from the subforum messages

Total for specification PostTests
Finished in 201 ms
6 examples, 0 failure, 0 error

# UserTests

### User should
  + be valid with firstname, lastname, email and password
  + be invalid without first name
  + be invalid without last name
  + be invalid with invalid email
  + be invalid without password
### Set user role should
  + change user's role if stronger than the current one
  + change user's role if weaker than the current one
  + moderate the forums of the new role with the previous one, if both of them are moderation

Total for specification UserTests
Finished in 197 ms
8 examples, 0 failure, 0 error

# RolesTests

### A moderator should
  + be stronger than a normal user
### A forum admin should
  + be stronger than a moderator
### God should
  + be stronger than a forum admin
### Transitivity roles should
  + success

Total for specification RolesTests
Finished in 144 ms
4 examples, 0 failure, 0 error

# SubForumTests

### Subforum should
  + be valid with name and at least one moderator
  + be invalid without name
  + be invalid without moderators
  + be invalid when moderators num doesn't meet the policy

Total for specification SubForumTests
Finished in 60 ms
4 examples, 0 failure, 0 error

# CommentTests

### Comment should
  + be valid with content
  + be invalid without content
### Comment.root should
  + return the root post of the comment
### Delete comment comments should
  + remove all the comment's comments successfully
  + remove all the comment's comments from the root post subforum's messages

Total for specification CommentTests
Finished in 110 ms
5 examples, 0 failure, 0 error

# ShinGimmelTests

### basic permission rules should
  + allows the given permissions but nothing else
  + allows the given permissions for any kind of resource
### restrict access to a specific kind of resource should
  + allows access to the restricted resource
  + block the given permission on other resources
### restrict access based on a predicate should
  + allows access to a resource that satisfies the predicate
  + block access if predicate was not satisfied
### rules derivation from single parent should
  + allows access to parent's permissions and child's permissions but nothing else
  + allows access to any kind of resource based on child's & parent's permissions
### rules derivation from multiple parents should
  + allows access to all parents' permissions and child's permissions but nothing else
### rules overridden of same kind should
  + give precedence to child's permissions
### reduce permissions by rules overridden of different kind should
  + combines parent's & child permissions
### extend permissions by rules overridden of different kinds should
  + combines parent's & child permissions

Total for specification ShinGimmelTests
Finished in 177 ms
12 examples, 0 failure, 0 error
Passed: Total 12, Failed 0, Errors 0, Passed 12

# RolePermissionsTests

### a normal user
  + can publish anything
  + can edit only messages he owns
  + can delete only messages he owns
  + can report other users
  + have publish, edit and delete permissions but nothing else

### a moderator
  + behave like a normal user
  + can ban users
  + can edit any post in forums that he moderates
  + can edit any comment in forums that he moderates
  + can delete any post in forums that he moderates
  + can delete any comment forums that he moderates
  + have publish, edit, delete and ban users but nothing else

### a forum admin
  + can publish anything
  + can ban users
  + can edit any post in any forum
  + can edit any comment in any forum
  + can delete any post in any forum
  + can delete any comment in any forum
  + can manage subforums moderators
  + can manage forum admins
  + can manage subforums
  + have publish, edit, delete, ban users, manage subforums moderators, manage forum admins, but nothing else

### god
  + behave like a forum admin
    + can publish anything
    + can ban users
    + can edit any post in any forum
    + can edit any comment in any forum
    + can delete any post in any forum
    + can delete any comment in any forum
    + can manage subforums moderators
    + can manage forum admins
    + can manage subforums
  + can manage forum policy
  + can manage user types
  + can do everything

Total for specification RolePermissionsTests
Finished in 57 ms
34 examples, 0 failure, 0 error

# ForumServiceIT

### Forum initialization should
  * pending("TBI") TBI

### User registration should
  + success for a valid user and password
  + store the password as SHA-1 digest
  + failed if user already registered
  + failed for invalid details
  + failed for an invalid password (doesn't meet the current password policy)
  + send verification code to user's email upon registration

### User login should
  + success for registered user and correct password
  + failed for registered user but incorrect password
  + failed for unregistered user

### User logout should
  * pending("because it's an API, so we don't have a session for the logged-in user. will be added with because it's an API, so we don't have a session for the logged-in user. will be added with the web layer

### Create subforum should
  + success for a valid subforum
  + failed for invalid subforum
  + failed when the subforum does not meet the forum policy
  + failed for unauthorized user (doesnt have permission to create subforum)

### Publish post should
  + success for a valid post, and the user who published is subscribed to the new post
  + add the published post to the user's posts
  + failed for an invalid post (no subject)
  + failed for guest user (doesn't have permission to publish)

### Publish comment should
  + success for a valid comment
  + notify post subscribers except the comment publisher
  + failed for an invalid comment
  + failed for guest user (doesn't have permission to publish)

### report moderator should
  + success if the user was already published a post in the moderator's subforum
  + success if the user was already published a comment in the moderator's subforum
  + failed if the user haven't post any message in the moderator's forum
  + failed if the user the report about is not a moderator in the given subforum
  + failed if the given `moderator` is not a moderator
  + failed for unauthorized user (doesn't have permission to report)

### subforum deletion should
  + success if the subforum exists // TBI: permissions
  + failed if the subforum doesnt exist
  + failed for unauthorized user (doesn't have permission to delete subforum)

### message deletion should
  * pending("because we don't have persistence layer.") because we don't have persistence layer.

### user types should
  * pending("because the requirements are unclear.") because the requirements are unclear.

Total for specification ForumServiceIT
Finished in 570 ms
34 examples, 0 failure, 0 error, 4 pending

# Summary

Passed: Total 102, Failed 0, Errors 0, Passed 102, Pending 4

[success] Total time: 4 s, completed Apr 26, 2015 12:26:56 PM
