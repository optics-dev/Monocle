package monocle

import monocle.syntax._

package object syntax extends Syntax

trait Syntax {

  object lens extends ToLensOps with ToAppliedLensOps

  object traversal extends ToTraversalOps with ToAppliedTraversalOps

  object iso extends ToAppliedIsoOps

}