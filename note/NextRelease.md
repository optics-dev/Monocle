## Next Release

*   SimpleOptional apply method takes a setter function from `(S, A) => A` instead of `(S, Option[A]) => A`
*   New constructors for simple optic with better type inference, see [example](../test/src/test/scala/monocle/LensSpec.scala#L13-L16)
*   Polymorphic optics instance reorganisation: All instances has been move from the companion objects of the
    polymorphic optics to packages:
    *   `monocle.std` for standard Scala classes
    *   `monocle.scalaz` for scalaz classes
    *   `monocle.generic` for shapeless classes
    Check out [example](../example/src/test/scala/other/ImportExample) to see how it affects imports
*   implicit not found annotations have been added to polymorphic optics to help user figuring out which import is missing
*   `monocle.scalaz.Either` has been renamed `monocle.scalaz.Disjunction` to avoid clashes wih `monocle.std.Either`
*   `setOption` and `modifyOption` were added to `Optional`
*   Flipped curried version of `set => setF`, `modify => modifyF`, `setOption => setOptionF`, `modifyOption => modifyOptionF`
    See [example](../example/src/test/scala/monocle/LensExample.scala#L43)
*   Add `Each`, `Index`, `Field1`, `Head`, `Tail` and `LastOption` instances for `scalaz.OneAnd`
*   Add `Each`, `HeadOption`, and `LastOption` for `scala.Some`
*   Upgrade scalaz dependency to 7.1
