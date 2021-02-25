package monocle

import monocle.function.GenericOptics
import monocle.macros.syntax.ApplyFocusSyntax
import monocle.state._
import monocle.std.StdInstances
import monocle.syntax.Syntaxes
import munit.DisciplineSuite

trait MonocleSuite
    extends DisciplineSuite
    with TestInstances
    with StdInstances
    with ApplyFocusSyntax
    with GenericOptics
    with Syntaxes
    with StateLensSyntax
    with StateOptionalSyntax
    with StateGetterSyntax
    with StateSetterSyntax
    with StateTraversalSyntax
    with ReaderGetterSyntax
