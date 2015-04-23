# Shin Gimmel

![Shin gimmel logo](https://dl.dropboxusercontent.com/u/4041100/github/shingimmel.png)

## Description
**Shin Gimmel** is a scala library for ACL (access control rules).

## Usage
Permissions definition (as singleton objects):
```scala
object Edit extends Permission
object Delete extends Permission
object Create extends Permission
```

Access rules set: 
```scala
val acl = rules {
    can(Edit)               // Edit every kind of resource
    can(Delete).a[Post]     // Delete Posts only
    can(Create) onlyWhen { p: Post => p.title == "hello shin gimmel!" }
}
```

Derive rules from other rules set
```scala
val creationRules = rules {
    can(Create).a[Forum]
}

val acl = rules {
    // extends creationRules
    deriveFrom(creationRules)
    
    can(Delete).a[Forum]
}
```

Check if a given permission is defined:
```scala
val user = User(...)
acl.isDefinedAt(Post, user) // returns true / false
```