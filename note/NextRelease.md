## Next Release

*   Cross build for scala 2.10.4 and 2.11.0
*   Update dependencies
    *   Remove scalacheck dependency on core - Laws moved to their own module
    *   scalacheck to 1.11.3
*   Add instances for scalaz `IList`
*   Add reverseModify on SimplePrism
*   Generalize reverse to type having a different type once reversed.
    Implemented instances for all tuple (2-6 in core, other in generic)
*   Rename `Head` -> `HeadOption` and `Last` -> `LastOption`
*   Add `Head` and `Last` functions which create a Lens to the first and last element respectively.
    Implemented instances for 2-6 tuple
*   Move Macro from core to their own module (macro)