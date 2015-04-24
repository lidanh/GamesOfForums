# Shin Gimmel

![Shin gimmel logo](https://dl.dropboxusercontent.com/u/4041100/github/shingimmel.png)

## Description
**Shin Gimmel** is a scala library for ACL (access control list).

## Usage
Permissions definition (as singleton objects):
```scala
object Edit extends Permission
object Delete extends Permission
object Create extends Permission
```

Scope definition (type of object that has the permissions. e.g. User):
```scala
case class User(name: String)
```

Access rules set: 
```scala
val acl = rulesFor[User] {
    can(Edit)               // Edit any kind of resource
    can(Delete).a[Post]     // Delete Posts only
    can(Create) onlyWhen { (u: User, p: Post) => u.posts.contains(p) } // Create only if the predicate is satisfied
}
```

Derive rules from other rules set
```scala
val creationRules = rulesFor[User] {
    can(Create).a[Forum]
}

val acl = rulesFor[User] {
    // extends creationRules
    deriveFrom(creationRules)
    
    // add more permissions
    can(Delete).a[Forum]
}
```

Check if a given permission is defined:
```scala
implicit val user = User(...)
val post = Post(...)

acl.isDefinedAt(Delete, post) // returns true / false (user is an implicit argument for isDefinedAt method)
```
