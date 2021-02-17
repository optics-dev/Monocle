package monocle

import monocle.function.GenericOptics
import monocle.state._
import monocle.std.StdInstances
import monocle.syntax.Syntaxes
import munit.DisciplineSuite

trait MonocleSuite
    extends DisciplineSuite
    with TestInstances
    with StdInstances
    with GenericOptics
    with Syntaxes
    with StateLensSyntax
    with StateOptionalSyntax
    with StateGetterSyntax
    with StateSetterSyntax
    with StateTraversalSyntax
    with ReaderGetterSyntax
