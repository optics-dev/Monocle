package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scalaz.IList

@State(Scope.Benchmark)
class StdTraversalBench {

  @Benchmark def caseClassGetAll() = IList(p.x, p.y, p.z)
  @Benchmark def caseClassSet()    = p.copy(x = 5, y = 5, z = 5)
  @Benchmark def caseClassModify() = p.copy(x = p.x + 1, y = p.y + 1, z = p.z + 1)

  @Benchmark def collectionGetAll() = IList.fromList(iMap.values)
  @Benchmark def collectionSet()    = iMap.map(_ => 12)
  @Benchmark def collectionModify() = iMap.map(_ + 1)

}
