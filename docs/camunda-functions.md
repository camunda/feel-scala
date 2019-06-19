---
title: Integration into Camunda BPM: Functions
---

## Camunda Functions

If the FEEL engine is used in the context of Camunda BPM then the following functions are available.

### now()

Returns the current date-time.

* result: date-time

```js
now()
// date and time("2018-04-29T09:30:00")
```

### currentUser()

Returns the authenticated user id, or `null` if no user is authenticated.

* result: string

```js
currentUser()
// "demo"
```

### currentUserGroups()

Returns the group ids of the authenticated user, or `null` if no user is authenticated.

* result: list of strings

```js
currentUserGroups()
// ["accounting", "sales"]
```
