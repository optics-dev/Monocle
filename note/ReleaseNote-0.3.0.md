## Release Note 0.3.0

*   Update dependencies
    *   scala to 2.10.4
    *   shapeless to 2.0.0
    *   macro paradise to 2.0.0
    *   quasi quotes to 2.0.0
*   Add apply4 to apply6 methods to Traversal
*   Add `Each` instance for 4 to 6 tuple of same type
*   Generic sub module:
    *   Add `Field1` to `Field6` instances for `HList`
    *   Add `SafeCast` instance for `Coproduct`, hence supporting Prism for sealed family
    *   Remove `_1`, `_2`, `_3` Lens using shapeless `Generic`, since composing `toHList` Iso and `Fields` Lens is equivalent