package monocle

import monocle.scalaz._


package object scalaz extends ScalazInstances

trait ScalazInstances
  extends DisjunctionFunctions
  with    IListInstances
  with    OneAndInstances
  with    TreeFunctions with TreeInstances
