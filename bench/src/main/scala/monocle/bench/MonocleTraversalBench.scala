package monocle.bench

import monocle.bench.BenchModel._
import monocle.{PTraversal, Traversal}
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import cats.instances.sortedMap._
import cats.instances.int._

import scala.collection.immutable.SortedMap

@State(Scope.Benchmark)
class MonocleTraversalBench {

  val point3Traversal = Traversal.apply3[Point3, Int](_.x, _.y, _.z)((x, y, z, _) => Point3(x, y, z))
  val iMapTraversal = PTraversal.fromTraverse[SortedMap[Int, ?], Int, Int]


  @Benchmark def caseClassGetAll() = point3Traversal.getAll(p)
  @Benchmark def caseClassSet()    = point3Traversal.set(5)(p)
  @Benchmark def caseClassModify() = point3Traversal.modify(_ + 1)(p)

  @Benchmark def collectionGetAll() = iMapTraversal.getAll(map)
  @Benchmark def collectionSet()    = iMapTraversal.set(12)(map)
  @Benchmark def collectionModify() = iMapTraversal.modify(_ + 1)(map)
}
