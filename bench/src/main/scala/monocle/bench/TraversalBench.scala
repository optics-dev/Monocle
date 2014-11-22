package monocle.bench

import monocle.{PTraversal, Traversal}
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.{IList, IMap}
import scalaz.std.anyVal._


@State(Scope.Benchmark)
class TraversalBench {

  case class Point3(x: Int, y: Int, z: Int)

  val point3Traversal = Traversal.apply3[Point3, Int](_.x, _.y, _.z)((x, y, z, _) => Point3(x, y, z))

  val p = Point3(2, 10, 24)

  @Benchmark def point3StdGetAll()       = IList(p.x, p.y, p.z)
  @Benchmark def point3TraversalGetAll() = point3Traversal.getAll(p)

  @Benchmark def point3StdSet()       = p.copy(x = 5, y = 5, z = 5)
  @Benchmark def point3TraversalSet() = point3Traversal.set(5)(p)

  @Benchmark def point3StdModify()       = p.copy(x = p.x + 1, y = p.y + 1, z = p.z + 1)
  @Benchmark def point3TraversalModify() = point3Traversal.modify(_ + 1)(p)


  val iMap = IMap.fromList(Stream.from(1).take(200).map(_ -> 5).toList)

  val iMapTraversal = PTraversal.fromTraverse[IMap[Int, ?], Int, Int]

  @Benchmark def collectionStdGetAll()       = IList.fromList(iMap.values)
  @Benchmark def collectionTraversalGetAll() = iMapTraversal.getAll(iMap)

  @Benchmark def collectionStdSet()       = iMap.map(_ => 12)
  @Benchmark def collectionTraversalSet() = iMapTraversal.set(12)(iMap)

  @Benchmark def collectionStdModify()       = iMap.map(_ + 1)
  @Benchmark def collectionTraversalModify() = iMapTraversal.modify(_ + 1)(iMap)


}