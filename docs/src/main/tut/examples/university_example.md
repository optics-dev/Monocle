---
layout: docs
title:  "University"
section: "examples_menu"
---
# University Example

Let's take a basic model of a `University` containing a few `Department`s where each `Department` has a budget
and a few `Lecturer`s.

```tut:silent
case class Lecturer(firstName: String, lastName: String, salary: Int)
case class Department(budget: Int, lecturers: List[Lecturer])
case class University(name: String, departments: Map[String, Department])

val uni = University("oxford", Map(
  "Computer Science" -> Department(45, List(
    Lecturer("john"  , "doe", 10),
    Lecturer("robert", "johnson", 16)
  )),
  "History" -> Department(30, List(
    Lecturer("arnold", "stones", 20)
  ))
))
```

## How to remove or add elements in a Map

Our university is having some financial issues and it has to close the History department.

First, we need to zoom into `University` to the departments field using a `Lens`

```tut:silent
import monocle.macros.GenLens  // require monocle-macro module

val departments = GenLens[University](_.departments)
```

then we zoom into the `Map` at the `History` key using `At` typeclass


```tut:silent
import monocle.function.At.at // to get at Lens
import monocle.std.map._      // to get Map instance for At
```

```tut
(departments andThenLens at("History")).set(None)(uni)
```

if instead we wanted to create a department, we would have used `set` with `Some`:

```tut:silent
val physics = Department(36, List(
  Lecturer("daniel", "jones", 12),
  Lecturer("roger" , "smith", 14)
))
```

```tut
(departments andThenLens at("Physics")).set(Some(physics))(uni)
```

## How to update a field in a nested case class

Let's have a look at a more positive scenario where all university lecturers get a salary increase.

First we need to generate a few `Lens`es in order to zoom in the interesting fields of our model.

```tut:silent
val lecturers = GenLens[Department](_.lecturers)
val salary = GenLens[Lecturer](_.salary)
```

We want to focus to all university lecturers, for this we can use `Each` typeclass as it provides a `Traversal`
which zooms into all elements of a container (e.g. `List`, `Vector` `Map`)

```tut:silent
import monocle.function.all._ // to get each and other typeclass based optics such as at or headOption
import monocle.Traversal
import monocle.unsafe.MapTraversal._ // to get Each instance for Map (SortedMap does not require this import)

val allLecturers: Traversal[University, Lecturer] = departments composeTraversal each andThenLens lecturers composeTraversal each
```

Note that we used `each` twice, the first time on `Map` and the second time on `List`.

```tut:book
(allLecturers andThenLens salary).modify(_ + 2)(uni)
```

## How to create your own Traversal

We realised that our data is not formatted correctly, in particular first and last name are not upper cased.
We can reuse the `Traversal` to all `Lecturer`s we previously created but this time we need to zoom into the first
character of both `firstName` and `lastName`.

You know the drill, first we need to create the `Lens`es we need.

```tut:silent
val firstName = GenLens[Lecturer](_.firstName)
val lastName  = GenLens[Lecturer](_.lastName)
```

Then, we can use `Cons` typeclass which provides both `headOption` and `tailOption` optics. In our case, we want
to use `headOption` to zoom into the first character of a `String`

```tut:silent
import monocle.std.string._ // to get String instance for Cons
```

```tut
val upperCasedFirstName = (allLecturers andThenLens firstName composeOptional headOption).modify(_.toUpper)(uni)
(allLecturers andThenLens lastName composeOptional headOption).modify(_.toUpper)(upperCasedFirstName)
```

It is annoying that we have to call `modify` on first name and then repeat the same action on last name. Ideally, we
would like to focus to both first and last name. To do that we need to create our own `Traversal`


```tut:silent
val firstAndLastNames = Traversal.apply2[Lecturer, String](_.firstName, _.lastName){ case (fn, ln, l) => l.copy(firstName = fn, lastName = ln)}
```

```tut
(allLecturers composeTraversal firstAndLastNames composeOptional headOption).modify(_.toUpper)(uni)
```
