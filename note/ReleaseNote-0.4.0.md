## Release Note 0.4.0

#### Modularity and build
*   Cross build for scala 2.10.4 and 2.11.0
*   Move Optic Laws from module core to law module
*   Move `Macro` from module core to macro module
*   Update dependencies
    *   Remove scalacheck, scala-reflect and scala-compiler dependencies from core
    *   upgrade scalacheck to 1.11.3
*   Add Mima support on core to check for binary compatibility between minor version for next releases

#### New major concept: Optional
*   Add `Optional` a new major concept that represents a 0-1 `Traversal`. `Optional` is a sub class of `Traversal` and
    a super class of `Lens` and `Prism`. Composing a `Lens` and a `Prism` creates an `Optional` instead of a `Traversal`,
    hence we maintain information that the resulting Optic zooms to an optional object and not a list
*   Rename `Head` -> `HeadOption`, `Last` -> `LastOption`, `Tail` -> `TailOption`and `Init` -> `InitOption`
*   Change `HeadOption`, `LastOption`, `TailOption`, `InitOption` Optic from `Traversal` to `Optional`

#### Functions
*   Generalize `Reverse`, `Tail` and `Init` from 1 to 2 types parameters.
*   Add `Head`, `Last`, `Tail`, `Init` functions with the same semantic than the Option (`HeadOption`, ...) variant except
    that their Optic is a `Lens` and not an `Optional`. This implies that `Head`, `Last`, `Tail`, `Init` can only be defined on
    elements with a mandatory head, last, tail and init. Implemented on 2-6 tuple and `HList`.
*   Implemented `Reverse` instances on all tuple and `HList`.
*   Implemented `Reverse`, `Head`, `Last`, `Tail`, `Init` instances on all tuple and `HList`.
*   Implemented `HeadOption`, `LastOption`, `TailOption`, `InitOption`, `Each`, `Index` and `FilterIndex` for scalaz `IList`.

#### Miscellaneous
*   Add reverseModify on `SimplePrism` as an alias for `getOption map modify`.
*   Add syntax for `Getter`, `Fold` and `Setter`.
*   Add non symbolic functions to use Optics as operators, e.g.
    `List(1,2,3) applyTraversal each getAll` instead of `List(1,2,3) |->> each getAll`
*   Add import for all syntax: `import monocle.syntax._`







