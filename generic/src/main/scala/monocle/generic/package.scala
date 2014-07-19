package monocle

import monocle.generic._


package object generic extends Generic

trait Generic
  extends CoProductInstances
  with    HListInstances
  with    TupleNInstances
