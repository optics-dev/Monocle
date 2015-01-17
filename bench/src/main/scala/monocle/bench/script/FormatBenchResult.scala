package monocle.bench.script

import scalaz.Maybe.Just
import scalaz.std.anyVal._
import scalaz.{Order, Maybe, IList, IMap, \/, INil, ICons}
import scalaz.syntax.std.option._
import scalaz.std.string._

sealed trait Impl
object Impl {
  implicit val ord = Order.orderBy[Impl, Int]{
    case STD       => 0
    case MACRO     => 1
    case MONOCLE   => 2
    case SCALAZ    => 3
    case SHAPELESS => 4
  }
  case object STD       extends Impl
  case object MACRO     extends Impl
  case object MONOCLE   extends Impl
  case object SCALAZ    extends Impl
  case object SHAPELESS extends Impl
}

final case class Method(value: String)

object Method {
  implicit val ord = Order.orderBy[Method, String](_.value)
}

final case class BenchLine(method: Method, impl: Impl, score: Long)
final case class BenchResult(method: Method, stdScore: Long, implScores: IMap[Impl, Long])


/**
 * Format raw jmh csv result into a more comprehensible csv or markdown representation
 */
object FormatBenchResult extends App {
  import Impl._

  val input  = "benchData/jmh-result.csv"
  val output = "benchData/formatted.csv"


  val benchLines: IList[BenchLine] = IList.fromList(
    scala.io.Source.fromFile(input).getLines().toList
      .map(parseLine)
      .collect{case Just(l) => l}
  )

  val benchResults: IMap[Method, Maybe[BenchResult]] =
    benchLines.groupBy(_.method).map(mkBenchResult)

  val resultList: IList[BenchResult] =
    IList.fromList(benchResults.values.flatMap(_.cata(List(_), Nil)))

  formatAsCSV(resultList).reverse.map(println)

  formatAsMD(resultList).reverse.map(println)


  def parseLine(line: String): Maybe[BenchLine] =
    for {
      columns <- extractBenchNameAndScore(line)
      (benchName, score) = columns
      impl    <- extractImpl(benchName)
      method  <- extractMethod(benchName)
    } yield BenchLine(method, impl, score)


  def extractBenchNameAndScore(line: String): Maybe[(String, Long)] =
    \/.fromTryCatchNonFatal{
      val columns = line.split(',')
      (columns(0), columns(4).toDouble.toLong)
    }.toMaybe

  def extractImpl(benchName: String): Maybe[Impl] =
    if(benchName.contains("Macro")) Maybe.just(MACRO)
    else if(benchName.contains("Monocle")) Maybe.just(MONOCLE)
    else if(benchName.contains("Scalaz")) Maybe.just(SCALAZ)
    else if(benchName.contains("Shapeless")) Maybe.just(SHAPELESS)
    else if(benchName.contains("Std")) Maybe.just(STD)
    else Maybe.empty

  def extractMethod(benchName: String): Maybe[Method] =
    \/.fromTryCatchNonFatal(
      benchName.split('.').last.init
    ).toMaybe.map(Method.apply)


  def mkBenchResult(lines: IList[BenchLine]): Maybe[BenchResult] = {
    val impls = lines.groupBy1(_.impl).map(_.head)
    impls.lookup(STD).toMaybe.map( stdLine =>
      BenchResult(stdLine.method, stdLine.score, (impls - STD).map(_.score))
    )
  }

  def format(results: IList[BenchResult]): IList[(String, String, String, String, String, String, String, String, String, String)] = {
    def f(l: Long): String =
      l.toString

    def fp(d: Double): String =
      "%.2f".format(d)

    def sc(implScores: IMap[Impl, Long], impl: Impl): String =
      implScores.lookup(impl).map(f).getOrElse("N/A")

    def scp(r: BenchResult, impl: Impl): String =
      r.implScores.lookup(impl).map(_.toDouble / r.stdScore * 100).map(fp).getOrElse("N/A")

    ("Method", "Monocle Macro / Std (%)", "Monocle / Std (%)", "Scalaz / Std (%)", "Shapeless / Std (%)", "Std (ops/s)", "Monocle Macro (ops/s)", "Monocle (ops/s)", "Scalaz (ops/s)", "Shapeless (ops/s)") ::
      results.map( r =>
        (r.method.value, scp(r, MACRO), scp(r, MONOCLE), scp(r, SCALAZ), scp(r, SHAPELESS), f(r.stdScore), sc(r.implScores, MONOCLE), sc(r.implScores, MONOCLE), sc(r.implScores, SCALAZ), sc(r.implScores, SHAPELESS))
      )
  }


  def formatAsCSV(results: IList[BenchResult]): IList[String] =
    format(results).map(r =>
      List(r._1, r._2, r._3, r._4, r._5, r._6, r._7, r._8).mkString(",")
    )

  def formatAsMD(results: IList[BenchResult]): IList[String] =
    format(results).map(r =>
      List(r._1, r._2, r._3, r._4, r._5, r._6, r._7, r._8).mkString("|", "|", "|")
    ) match {
      case INil()      => INil()
      case ICons(h, t) => h :: List.fill(8)("-----------:").mkString("|", "|", "|")  :: t
    }

}

