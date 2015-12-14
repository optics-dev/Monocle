---
layout: default
title:  "University Example"
section: "university_example"
pageSource: "https://raw.githubusercontent.com/julien-truffaut/Monocle/master/docs/src/main/tut/university_example.md"
---
# University Example

Let's take a basic model of a `University` containing a few `Department`s where each `Department` has a budget 
and a few `Lecturer`s.

```scala
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

```scala
import monocle.macros.GenLens  // require monocle-macro module

val departments = GenLens[University](_.departments)
```

then we zoom into the `Map` at the `History` key using `At` typeclass


```scala
import monocle.function.At.at // to get at Lens
import monocle.std.map._      // to get Map instance for At
```

```scala
scala> (departments composeLens at("History")).set(None)(uni)
res2: University = University(oxford,Map(Computer Science -> Department(45,List(Lecturer(john,doe,10), Lecturer(robert,johnson,16)))))
```

if instead we wanted to create a department, we would have used `set` with `Some`:
 
```scala
val physics = Department(36, List(
  Lecturer("daniel", "jones", 12),
  Lecturer("roger" , "smith", 14)
))
```

```scala
scala> (departments composeLens at("Physics")).set(Some(physics))(uni)
res3: University = University(oxford,Map(Computer Science -> Department(45,List(Lecturer(john,doe,10), Lecturer(robert,johnson,16))), History -> Department(30,List(Lecturer(arnold,stones,20))), Physics -> Department(36,List(Lecturer(daniel,jones,12), Lecturer(roger,smith,14)))))
```

## How to update a field in a nested case class

Let's have a look at a more positive scenario where all university lecturers get a salary increase.

First we need to generate a few `Lens`es in order to zoom in the interesting fields of our model.

```scala
val lecturers = GenLens[Department](_.lecturers)
val salary = GenLens[Lecturer](_.salary)
```

We want to focus to all university lecturers, for this we can use `Each` typeclass as it provides a `Traversal`
which zooms into all elements of a container (e.g. `List`, `Vector` `Map`)

```scala
import monocle.function.all._ // to get each and other typeclass based optics such as at or headOption
import monocle.std.list._     // to get List instance for Each
import monocle.std.map._      // to get Map instance for Each
import monocle.Traversal

val allLecturers: Traversal[University, Lecturer] = departments composeTraversal each composeLens lecturers composeTraversal each
```

Note that we used `each` twice, the first time on `Map` and the second time on `List`.

```scala
scala> (allLecturers composeLens salary).modify(_ + 2)(uni)
res5: University = University(oxford,Map(Computer Science -> Department(45,List(Lecturer(john,doe,12), Lecturer(robert,johnson,18))), History -> Department(30,List(Lecturer(arnold,stones,22)))))
```

## How to create your own Traversal

We realised that our data is not formatted correctly, in particular first and last name are not upper cased.
We can reused the `Traversal` to all `Lecturer`s we previously created but this time we need to zoom into the first 
character of both `firstName` and `lastName`.

You know the drill, first we need to create the `Lens`es we need.

```scala
val firstName = GenLens[Lecturer](_.firstName)
val lastName  = GenLens[Lecturer](_.lastName)
```

Then, we can use `Cons` typeclass which provides both `headOption` and `tailOption` optics. In our case, we want
to use `headOption` to zoom into the first character of a `String`

```scala
import monocle.std.string._ // to get String instance for Cons
```

```scala
scala> val upperCasedFirstName = (allLecturers composeLens firstName composeOptional headOption).modify(_.toUpper)(uni)
upperCasedFirstName: University = University(oxford,Map(Computer Science -> Department(45,List(Lecturer(John,doe,10), Lecturer(Robert,johnson,16))), History -> Department(30,List(Lecturer(Arnold,stones,20)))))

scala> (allLecturers composeLens lastName composeOptional headOption).modify(_.toUpper)(upperCasedFirstName)
res6: University = University(oxford,Map(Computer Science -> Department(45,List(Lecturer(John,Doe,10), Lecturer(Robert,Johnson,16))), History -> Department(30,List(Lecturer(Arnold,Stones,20)))))
```

It is annoying that we have to call `modify` on first name and then repeat the same action on last name. Ideally, we 
would like to focus to both first and last name. To do that we need to create our own `Traversal`


```scala
val firstAndLastNames = Traversal.apply2[Lecturer, String](_.firstName, _.lastName){ case (fn, ln, l) => l.copy(firstName = fn, lastName = ln)}
```

```scala
scala> (allLecturers composeTraversal firstAndLastNames composeOptional headOption).modify(_.toUpper)(uni)
res7: University = University(oxford,Map(Computer Science -> Department(45,List(Lecturer(John,Doe,10), Lecturer(Robert,Johnson,16))), History -> Department(30,List(Lecturer(Arnold,Stones,20)))))
```
