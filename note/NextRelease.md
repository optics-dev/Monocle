## Next Release

#### Build
*   Cross build for scala 2.10.4 and 2.11.0
*   Update dependencies
    *   Remove scalacheck dependency on core - Laws moved to their own module
    *   scalacheck to 1.11.3


#### Modularity
*   Move `Macro` from core to its own module (macro)
*   Add syntax for getter, fold and setter
*   Add non symbolic function to use Optics (`Lens`, `Traversal`, etc) as operators


#### Functions
*   Add `Optional` a new major concept that represents a 0-1 `Traversal`. `Optional` is a sub class of `Traversal` and
    a super class of `Lens` and `Prism`. Composing a `Lens` and a `Prism` creates an `Optional` instead of a `Traversal`,
    hence we maintain information that the resulting Optic zooms on an optional object
*   Rename `Head` -> `HeadOption`, `Last` -> `LastOption`, `Tail` -> `TailOption`and `Init` -> `InitOption`
*   Change Optic `HeadOption`, `LastOption`, `TailOption`, `InitOption` from `Traversal` to `Optional`
*   Add `Head`, `Last`, `Tail`, `Init` functions with the same semantic than the Option (`HeadOption`, ...) variant except
    that they are `Lens` and not `Optional`. This implies that `Head`, `Last`, `Tail`, `Init` can only be defined on
    elements with a mandatory head, last, tail and init. Implemented on 2-6 tuple
*   Generalize reverse from 1 to 2 types parameters.
    Implemented instances for all tuple (2-6 in core, other in generic)
*   Add instances for scalaz `IList`
*   Add reverseModify on `SimplePrism` as an alias for `getOption map modify`







