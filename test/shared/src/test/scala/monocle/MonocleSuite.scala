package monocle

import monocle.function.GenericOptics
import monocle.generic.GenericInstances
import monocle.refined.RefinedInstances
import monocle.state._
import monocle.std.StdInstances
import monocle.syntax.Syntaxes
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FunSuiteDiscipline
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

trait MonocleSuite extends AnyFunSuite
                      with Configuration
                      with FunSuiteDiscipline
                      with Matchers
                      with TestInstances
                      with StdInstances
                      with GenericOptics
                      with GenericInstances
                      with RefinedInstances
                      with Syntaxes
                      with StateLensSyntax
                      with StateOptionalSyntax
                      with StateGetterSyntax
                      with StateSetterSyntax
                      with StateTraversalSyntax
                      with ReaderGetterSyntax
