package monocle.bench.lens

import java.io.{File, FileWriter}
import java.time.LocalDateTime

import org.jfree.chart.ChartUtilities
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.{ChainedOptionsBuilder, Options, OptionsBuilder}

import scala.util.Properties._
import scala.util.Try

object LensBench {

  def main(args: Array[String]): Unit = {
    val config = Config(args).getOrElse(
      sys.error("requires config option xs, s, m or l for small, medium or long")
    )

    val runner = new Runner(config.jmhOptions)
    val matrix = MatrixFormatter.parse(runner.run())
    val normalisedMatrix = matrix.normalised
    val now = LocalDateTime.now()

    val f = new FileWriter("lens.csv", true)

    (List(s"date,$now", s"config,${config.name}", s"scala,$versionNumberString") ++
      MatrixFormatter.toCSVRaw(matrix) ++
      MatrixFormatter.toCSVRelative(normalisedMatrix) ++
      List(""))
      .foreach(l => f.write(l + "\n"))

    f.close()

    ChartUtilities.saveChartAsJPEG(new File(s"lens-$now.png"), Chart.save(matrix), 1200, 400)
    ChartUtilities.saveChartAsJPEG(new File(s"lens-normalised-$now.png"), Chart.save(normalisedMatrix), 1200, 400)
  }

}

case class Config(name: String, builder: ChainedOptionsBuilder) {
  def exclude(regex: String): Config = copy(builder = builder.exclude(regex))
  def jmhOptions: Options =
    builder
      .include("monocle.bench.lens.*")
      .build
}

object Config {
  def apply(args: Array[String]): Option[Config] = args.headOption.map{
    case "xs" => Config.extraShort
    case "s"  => Config.short
    case "m"  => Config.medium
    case "l"  => Config.long
  }.map(c => Try(args(1)).toOption.fold(c){
    case "monocle" => c.exclude(".*SHAPELESS.*").exclude(".*SCALAZ.*")
    case "scalaz"  => c.exclude(".*SHAPELESS.*")
    case _         => c
  })

  val extraShort = Config("extra-short", new OptionsBuilder()
    .warmupIterations(0)
    .measurementIterations(1)
    .forks(1))

  val short = Config("short", new OptionsBuilder()
    .warmupIterations(3)
    .measurementIterations(3)
    .forks(1))

  val medium = Config("medium", new OptionsBuilder()
    .warmupIterations(10)
    .measurementIterations(10)
    .forks(5)
    .threads(Runtime.getRuntime.availableProcessors))

  val long = Config("long", new OptionsBuilder()
    .warmupIterations(20)
    .measurementIterations(20)
    .forks(10)
    .threads(Runtime.getRuntime.availableProcessors))
}

