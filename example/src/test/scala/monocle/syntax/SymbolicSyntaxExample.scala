package monocle.syntax

import monocle.macros.Lenser
import org.specs2.scalaz.Spec
import monocle.{SimplePrism, SimpleLens}
import scalaz.Maybe
import scalaz.syntax.maybe._
import monocle.function._
import monocle.std._

class SymbolicSyntaxExample extends Spec {

  case class Store(articles: List[Article])

  sealed trait Article
  case class Table(wood: String) extends Article
  case class Sofa(color: String, price: Int) extends Article

  val _articles = SimpleLens((_: Store).articles)((as, s) => s.copy(articles = as))
  val _sofa  = SimplePrism[Article, Sofa ]{ case s: Sofa  => s.just; case _ => Maybe.empty}(identity)

  val sofaLenser = Lenser[Sofa]
  val (_color, _price) = (sofaLenser(_.color), sofaLenser(_.price))



  "Symbols can replace composeX and applyX methods" in {
    val myStore = Store(List(Sofa("Red", 10), Table("oak"), Sofa("Blue", 26)))

    (myStore &|-> _articles ^|-? headMaybe ^<-? _sofa ^|-> _color getMaybe) ==== "Red".just

    (myStore &|-> _articles ^<-> iListToList.reverse ^|->> each ^<-? _sofa ^|-> _price modify(_ / 2)) === Store(
      List(Sofa("Red", 5), Table("oak"), Sofa("Blue", 13))
    )

    (myStore.articles &|-? index(1) ^<-? _sofa getMaybe) ==== Maybe.empty[Sofa]
  }
  
}
