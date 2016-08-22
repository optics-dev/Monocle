package monocle.bench.lens

import org.openjdk.jmh.results.RunResult

import scala.collection.JavaConverters._
import scala.language.existentials

object MatrixFormatter {

  def parse(collection: java.util.Collection[RunResult]): Matrix = {
    val results = collection.asScala.toList

    results.foldLeft(Matrix.empty)( (acc, r) =>
      (for {
        method <- extractMethod(r)
        impl   <- extractImpl(r)
        res     = r.getPrimaryResult
      } yield acc.addResult(method, impl, Result(res.getScore, res.getScoreUnit, res.getScoreError))
      ).getOrElse{
         println(s"Could not extract method and impl from ${r.getParams.getBenchmark}")
         acc
      }
    )
  }

  def toCSVRaw(matrix: Matrix): List[String] =
    (("Method" :: Impl.all.map(_.toString)) :: Method.all.map{ method =>
      method.toString :: Impl.all.map(impl => matrix.get(method, impl).fold("N/A")(r => format(r.score)))
    }).map(_.mkString(","))

  def toCSVRelative(matrix: NormalisedMatrix): List[String] =
    (("Method" :: Impl.all.map(_.toString)) :: Method.all.map(method =>
      method.toString :: Impl.all.map(impl => matrix.get(method, impl).fold("N/A")(format))
    )).map(_.mkString(","))

  private def format(d: Double): String =
    "%1.2f".format(d)

  private def extractMethod(r: RunResult): Option[Method] =
    Method.all.find(m => r.getParams.getBenchmark.contains(m.toString))

  private def extractImpl(r: RunResult): Option[Impl] =
    Impl.all.find(m => r.getParams.getBenchmark.contains(m.toString))

}

case class Result(score: Double, unit: String, error: Double)

sealed trait Method extends Product with Serializable
object Method {
  val all = List(Get0, Get3, Get6, Set0, Set3, Set6, Modify0, Modify3, Modify6, ModifyF0, ModifyF3, ModifyF6)
  case object Get0     extends Method
  case object Get3     extends Method
  case object Get6     extends Method
  case object Set0     extends Method
  case object Set3     extends Method
  case object Set6     extends Method
  case object Modify0  extends Method
  case object Modify3  extends Method
  case object Modify6  extends Method
  case object ModifyF0 extends Method
  case object ModifyF3 extends Method
  case object ModifyF6 extends Method

  implicit val ordering: Ordering[Method] = Ordering.by[Method, Int](all.indexOf)
}

sealed trait Impl extends Product with Serializable
object Impl {
  val all = List(STD, MACRO, MO, SCALAZ, SHAPELESS)
  case object STD       extends Impl
  case object MACRO     extends Impl
  case object MO        extends Impl
  case object SCALAZ    extends Impl
  case object SHAPELESS extends Impl

  implicit val ordering: Ordering[Impl] = Ordering.by[Impl, Int](all.indexOf)
}

