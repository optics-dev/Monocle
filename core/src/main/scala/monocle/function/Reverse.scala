package monocle.function

import monocle.SimpleIso
import scalaz.{IList, Tree}

trait Reverse[S] {

  /** Creates an Iso from S to a reversed S */
  def reverse: SimpleIso[S, S]

}

object Reverse extends ReverseInstances

trait ReverseInstances {

  def apply[S](_reverse: S => S): Reverse[S] = new Reverse[S] {
    def reverse: SimpleIso[S, S] = SimpleIso[S, S](_reverse, _reverse)
  }

  def reverse[S](implicit ev: Reverse[S]): SimpleIso[S, S] = ev.reverse

  implicit def listReverse[A]  : Reverse[List[A]]   = apply[List[A]](_.reverse)
  implicit def iListReverse[A] : Reverse[IList[A]]  = apply[IList[A]](_.reverse)
  implicit def streamReverse[A]: Reverse[Stream[A]] = apply[Stream[A]](_.reverse)
  implicit def stringReverse[A]: Reverse[String]    = apply[String](_.reverse)
  implicit def vectorReverse[A]: Reverse[Vector[A]] = apply[Vector[A]](_.reverse)

  implicit def treeReverse[A]: Reverse[Tree[A]] = new Reverse[Tree[A]] {
    def reverse = SimpleIso[Tree[A], Tree[A]](reverseTree, reverseTree)
    private def reverseTree(tree: Tree[A]): Tree[A] = Tree.node(tree.rootLabel, tree.subForest.reverse.map(reverseTree))
  }

}
