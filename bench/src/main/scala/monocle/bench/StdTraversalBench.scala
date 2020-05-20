package monocle.bench

import monocle.bench.BenchModel._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class StdTraversalBench {
  @Benchmark def caseClassGetAll() = List(p.x, p.y, p.z)
  @Benchmark def caseClassSet()    = p.copy(x = 5, y = 5, z = 5)
  @Benchmark def caseClassModify() = p.copy(x = p.x + 1, y = p.y + 1, z = p.z + 1)

  @Benchmark def collectionGetAll() = map.values.toList
  @Benchmark def collectionSet()    = map.map(_ => 12)
  @Benchmark def collectionModify() = map.map { case (k, v) => k -> (v + 1) }
}
