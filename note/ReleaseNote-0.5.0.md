## Release Note 0.5.0

#### Build and Dependencies

*   Upgrade scalaz dependency from 7.0.6 to 7.1
*   Upgrade specs2 dependency from 2.3.11 to 2.4
*   Build with scala 2.11.2
*   Use sbt-typelevel plugin

#### Breaking Changes

*   SimpleOptional apply method takes a setter function from `(S, A) => A` instead of `(S, Option[A]) => A`
*   Polymorphic optics instance reorganisation: All instances has been move from the companion objects of the
    polymorphic optics to packages:
    *   `monocle.std` for standard Scala and scalaz classes
    *   `monocle.generic` for shapeless classes
    
    Check out this [example](../example/src/test/scala/other/ImportExample) to see how it affects imports
*   reverseModify has been moved from Monocle package object to `Prism` syntax
    
#### New Features

*   Add setOption and modifyOption for `Optional`
*   Flipped curried version of `set => setF`, `modify => modifyF`, `setOption => setOptionF`, `modifyOption => modifyOptionF`
    See [example](../example/src/test/scala/monocle/LensExample.scala#L43)
*   New constructors for simple optics with better type inference, see [example](../test/src/test/scala/monocle/LensSpec.scala#L13-L16)    
*   implicit not found annotations have been added to polymorphic optics to help user figuring out which import is missing
*   `monocle.scalaz.Either` has been renamed `monocle.scalaz.Disjunction` to avoid clashes wih `monocle.std.Either`
*   Add `Each`, `Index`, `Field1`, `Head`, `Tail` and `LastOption` instances for `scalaz.OneAnd`
*   Add `Each`, `HeadOption`, and `LastOption` for `scala.Some`
