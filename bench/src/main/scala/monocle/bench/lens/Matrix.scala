package monocle.bench.lens

import monocle.bench.lens.Impl.STD

import scala.util.Try

case class Row(impls: Map[Impl, Result])
case class Matrix(value: Map[Method, Row]) {
  def addResult(method: Method, impl: Impl, result: Result): Matrix =
    value.get(method).fold(this)(row =>
      Matrix(value + (method -> Row(row.impls + (impl -> result))))
    )
  def get(method: Method, impl: Impl): Option[Result] =
    value.get(method).flatMap(_.impls.get(impl))

  def normalised: NormalisedMatrix =
    NormalisedMatrix(value.foldLeft(Map.empty[Method, NormalisedRow]) { case (acc, (method, row)) =>
      row.impls.get(STD).fold(acc)(stdRes =>
        acc + (method -> NormalisedRow(
          row.impls.map{ case (impl, implRes) =>
            impl -> Try(stdRes.score / implRes.score).toOption
          }.collect{ case (impl, Some(ratio)) => impl -> ratio }
        ))
      )
    })
}
object Matrix {
  val empty: Matrix = Matrix(Method.all.map(_ -> Row(Map.empty)).toMap)
}

case class NormalisedMatrix(value: Map[Method, NormalisedRow]) {
  def get(method: Method, impl: Impl): Option[Double] =
    value.get(method).flatMap(_.impls.get(impl))
}
case class NormalisedRow(impls: Map[Impl, Double])
