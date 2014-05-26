## Next Release

*   Cross build for scala 2.10.4 and 2.11.0
*   Update dependencies
    *   Remove scalacheck dependency on core - Laws moved to their own module
    *   scalacheck to 1.11.3
*   Add instances for scalaz `IList`
*   Add reverseModify on `SimplePrism`
*   Generalize reverse, head, last, tail, and init from 1 to 2 types parameters.
    Implemented instances for all tuple (2-6 in core, other in generic)
*   Rename `Head` -> `HeadOption`, `Last` -> `LastOption`, `Tail` -> `TailOption`and `Last` -> `InitOption`
*   Add `Head` and `Last` functions which create a Lens to the first and last element respectively.
    Implemented instances for 2-6 tuple
*   Move `Macro` from core to its own module (macro)
*   Add syntax for getter, fold and setter
*   Add non symbolic function to use Optics (`Lens`, `Traversal`, etc) as operators
*   Add `Optional`, new major concept that represents a 0-1 `Traversal`
*   Refactor `HeadOption`, `LastOption`, `TailOption`, `InitOption` from `Traversal` to `Optional`