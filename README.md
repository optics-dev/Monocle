![Monocle Logo](https://raw.github.com/julien-truffaut/Monocle/master/image/logo.png)<br>
Monocle is a Scala lens library greatly inspired by Haskell [Lens](https://github.com/ekmett/lens).
### Build
[![Build Status](https://api.travis-ci.org/julien-truffaut/Monocle.png?branch=master)](https://travis-ci.org/julien-truffaut/Monocle)

```scala
resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"  % "0.3.0" // or 0.4-SNAPSHOT
)
```
### Usage
#### Lens
 ```scala
  case class Character(_name: String, _health: Int, _location: (Int, Int))

  import monocle.Macro

  val health   = Macro.mkLens[Character, Int]("_health")
  val location = Macro.mkLens[Character, (Int, Int)]("_location")

  val barbarian = Character("Krom" , 30, (8,13))

  health.get(barbarian) == 30
  health.set(barbarian, 32)       == Character("Krom" , 32, (8,13))
  health.modify(barbarian, _ + 1) == Character("Krom" , 31, (8,13))

  import monocle.function.Fields._

  (location composeLens _1).set(barbarian, 0) == Character("Krom" , 31, (0,13))
```
#### Traversal
 ```scala
  case class Game(_score: Int, _players: List[Character])

  val players = Macro.mkLens[Game, List[Character]]("_players")

  val barbarian = Character("Krom" , 30, (8,13))
  val wizard    = Character("Waza" , 12, (6,1))

  val dnd = Game(10, List(barbarian, wizard))

  import monocle.function.Each._

  (players composeTraversal each composeTraversal health).getAll(dnd) == List(30, 12)

  // reduce by 2 the health points of all players
  (players composeTraversal each composeTraversal health).modify(dnd, _ - 2) ==
    Game(10, List(Character("Krom" , 28, (8,13)), Character("Waza" , 10, (6,10))))

  // generate all possible legal moves
  def legalMoves(n: Int): List[Int] = List(n - 1, n + 1).filter(_ > 0)
  (players composeTraversal each composeTraversal location composeTraversal each).multiLift(dnd, legalMoves)

  // or with some syntax sugar
  import monocle.syntax.lens._
  dnd |-> players |->> each |->> location |->> each multiLift legalMoves
```
#### Overview
![Class Diagram](https://raw.github.com/julien-truffaut/Monocle/master/image/class-diagram.png)<br>
#### Sub Projects
Core contains the main library concepts: Lens, Traversal, Prism, Iso, Getter and Setter.
Core only depends on [scalaz](https://github.com/scalaz/scalaz) for type classes.

Law defines Iso, Lens, Prism, Setter and Traversal laws using [scalacheck](http://www.scalacheck.org/).

Generic is an experiment to provide highly generalised Lens and Iso using HList from [shapeless](https://github.com/milessabin/shapeless).
Generic focus is on neat abstraction but that may come at additional runtime or compile time cost.

Example shows how other sub projects can be used.
#### Contributor Handbook
We are happy to have as many people as possible contributing to Monocle.
Therefore, we made this small workflow to simplify the process:

1.   Select or create an issue (issues tagged with label "padawan-friendly" are designed for Scala novice)
2.   Comment on the issue letting everyone knows that you are working on it.
3.   Fork Monocle
4.   Work on your fork until you are satisfied (label your commits with issue number)
5.   Submit a [pull request](https://help.github.com/articles/using-pull-requests)
6.   We will review your pull request and merge it back to master

If you have any questions, we have irc channel on [freenode](http://webchat.freenode.net/) #scala-monocle and a [mailing group](https://groups.google.com/forum/#!forum/scala-monocle)

Thank you for you contribution!
### Contributors
Julien Truffaut - [@JulienTruffaut](https://twitter.com/JulienTruffaut "@JulienTruffaut") </a><br>
Ross Huggett - ross.huggett@gmail.com / [@rosshuggett](http://twitter.com/rosshuggett "@rosshuggett") </a><br>
Ilan Godik - ilan3580@gmail.com / [NightRa](https://github.com/NightRa "NightRa") </a><br>
### Requirements
Scala 2.10.2 and SBT 0.13.<br>
