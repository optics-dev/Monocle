package monocle.bench

import monocle.bench.BenchModel._
import monocle.{PTraversal, Traversal}
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.IMap

@State(Scope.Benchmark)
class MonocleTraversalBench {

  val point3Traversal = Traversal.apply3[Point3, Int](_.x, _.y, _.z)((x, y, z, _) => Point3(x, y, z))
  val iMapTraversal = PTraversal.fromTraverse[IMap[Int, ?], Int, Int]


  @Benchmark def caseClassGetAll() = point3Traversal.getAll(p)
  @Benchmark def caseClassSet()    = point3Traversal.set(5)(p)
  @Benchmark def caseClassModify() = point3Traversal.modify(_ + 1)(p)

  @Benchmark def collectionGetAll() = iMapTraversal.getAll(iMap)
  @Benchmark def collectionSet()    = iMapTraversal.set(12)(iMap)
  @Benchmark def collectionModify() = iMapTraversal.modify(_ + 1)(iMap)
}