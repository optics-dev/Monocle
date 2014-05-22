package monocle.function

import monocle.SimpleIso
import scalaz.{IList, Tree}

trait Reverse[S, A] {

  /** Creates an Iso from S to a reversed S */
  def reverse: SimpleIso[S, A]

}

object Reverse extends ReverseInstances

trait ReverseInstances {

  def simple[S](_reverse: S => S): Reverse[S, S] = new Reverse[S, S] {
    def reverse: SimpleIso[S, S] = SimpleIso[S, S](_reverse, _reverse)
  }

  def reverse[S, A](implicit ev: Reverse[S, A]): SimpleIso[S, A] = ev.reverse

  implicit def listReverse[A]  : Reverse[List[A]  , List[A]]   = simple[List[A]](_.reverse)
  implicit def iListReverse[A] : Reverse[IList[A] , IList[A]]  = simple[IList[A]](_.reverse)
  implicit def streamReverse[A]: Reverse[Stream[A], Stream[A]] = simple[Stream[A]](_.reverse)
  implicit def stringReverse[A]: Reverse[String   , String]    = simple[String](_.reverse)
  implicit def vectorReverse[A]: Reverse[Vector[A], Vector[A]] = simple[Vector[A]](_.reverse)

  implicit def treeReverse[A]: Reverse[Tree[A], Tree[A]] = new Reverse[Tree[A], Tree[A]] {
    def reverse = SimpleIso[Tree[A], Tree[A]](reverseTree, reverseTree)
    private def reverseTree(tree: Tree[A]): Tree[A] = Tree.node(tree.rootLabel, tree.subForest.reverse.map(reverseTree))
  }

  implicit def pairReverse[A, B]: Reverse[(A, B), (B, A)] = new Reverse[(A, B), (B, A)] {
    def reverse = SimpleIso[(A, B), (B, A)](_.swap, _.swap)
  }

  implicit def tripleReverse[A, B, C]: Reverse[(A, B, C), (C, B, A)] = new Reverse[(A, B, C), (C, B, A)] {
    def reverse = SimpleIso[(A, B, C), (C, B, A)](t => (t._3, t._2, t._1), t => (t._3, t._2, t._1))
  }

  implicit def quadrupleReverse[A, B, C, D]: Reverse[(A, B, C, D), (D, C, B, A)] = new Reverse[(A, B, C, D), (D, C, B, A)] {
    def reverse = SimpleIso[(A, B, C, D), (D, C, B, A)](t => (t._4, t._3, t._2, t._1), t => (t._4, t._3, t._2, t._1))
  }

  implicit def quintupleReverse[A, B, C, D, E]: Reverse[(A, B, C, D, E), (E, D, C, B, A)] = new Reverse[(A, B, C, D, E), (E, D, C, B, A)] {
    def reverse = SimpleIso[(A, B, C, D, E), (E, D, C, B, A)](t => (t._5, t._4, t._3, t._2, t._1), t => (t._5, t._4, t._3, t._2, t._1))
  }

  implicit def sixtupleReverse[A, B, C, D, E, F]: Reverse[(A, B, C, D, E, F), (F, E, D, C, B, A)] = new Reverse[(A, B, C, D, E, F), (F, E, D, C, B, A)] {
    def reverse = SimpleIso[(A, B, C, D, E, F), (F, E, D, C, B, A)](t => (t._6, t._5, t._4, t._3, t._2, t._1), t => (t._6, t._5, t._4, t._3, t._2, t._1))
  }

}
