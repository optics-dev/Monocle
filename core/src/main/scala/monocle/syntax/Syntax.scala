package monocle
package syntax

trait Syntax {

  object lens      extends ToLensOps      with ToAppliedLensOps
  object traversal extends ToTraversalOps with ToAppliedTraversalOps

}
