## Release Note 0.2.0

*   Add generic Lens, Traversal, Iso and Prism in monocle.function:
    *   `_1`, `_2`, ..., `_6`, Lens to focus on the ith element of a tuple. Implemented for tuple of size 6 or less
    *   `atBit(i: Int)`, Lens to focus on ith Bit. Implemented for Boolean, Byte, Char, Int and Long
    *   `safeCast`, Prism defining a safe down casting between two types. Implemented for most primitive type and String
    *   `curry` and `uncurry`, Iso for functions with up to 5 parameters
    *   `head` and `last`, Traversal to the first and last element respectively. Implemented for std List, Vector, Stream, Option and String
    *   `tail` and `init`, Traversal to all elements except first and all elements except last respectively.
        Implemented for std List, Vector, Stream and String
    *   `reverse`, Iso that reverses the order of elements. Implemented for std List, Vector, Stream, String and scalaz Tree
    *   `index(i: Int)`, Traversal to focus on ith element. Implemented for std List, Vector, Stream, Map and String
    *   `each`, Traversal to focus on all elements. Implemented for std List, Vector, Stream, Option, Map, String, pair and triple of same type and scalaz tree
    *   `filterIndex(predicate: Index => Boolean)`, Traversal to focus on all elements with index verifying predicate.
        Implemented for List, Vector, Stream, Map and String
    *   `at(key: K)`, Lens to focus on an optional element at a key, strictly more powerful than index as it permits to
        add and delete elements. Implemented for Map
*   Alias for Iso, Prism, Lens and Traversal composition:
    *   `<->  == composeIso`
    *   `?->  == composePrism`
    *   `|->  == composeLens`
    *   `|->> == composeTraversal`
*   Start to experiment using shapeless to generate Iso to `HList` in generic sub module
*   Add documentation in examples and improve landing page
