## Next Release

*   Cross build for scala 2.10.4 and 2.11.0
*   Update dependencies
    *   Remove scalacheck dependency on core - Laws moved to their own module
    *   scalacheck to 1.11.3
*   Add instances for scalaz `IList`
*   Add reverseModify on SimplePrism
*   Generalize reverse to type having a different type once reversed. Added instances for tuple 2-6