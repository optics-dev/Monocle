## Release Note 0.5.1

#### Macros

*   Add `@Lenses` Macro annotation to generate `SimpleLens` automatically for case classes
*   Add `Lenser` Macro to generate `SimpleLens` with a better IDE support than Macro annotation
*   Deprecate `mkLens` Macro as `Lenser` is strictly more powerful  

#### Bug Fix

*   [Fix](https://github.com/julien-truffaut/Monocle/commit/a544743ca439b485ac2be178f290a85bbf6c2c80) `SafeCast` between `String` to `Int`