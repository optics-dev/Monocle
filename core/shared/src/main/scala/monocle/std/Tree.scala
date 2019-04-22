package monocle.std

import monocle.Lens
import monocle.function.all._

import scala.annotation.tailrec
import scalaz.Tree
import scalaz.Tree.{Leaf, Node}

object tree extends TreeOptics

trait TreeOptics {

  final def rootLabel[A]: Lens[Tree[A], A] =
    Lens[Tree[A], A](_.rootLabel)(l => tree => Node(l, tree.subForest))

  final def subForest[A]: Lens[Tree[A], LazyList[Tree[A]]] =
    Lens[Tree[A], LazyList[Tree[A]]](_.subForest)(children => tree => Node(tree.rootLabel, children))

  final def leftMostLabel[A]: Lens[Tree[A], A] = {
    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case e if e.isEmpty  => tree.rootLabel
      case x #:: xs => _get(x)
    }

    def _set(newLeaf: A)(tree: Tree[A]): Tree[A] = tree.subForest match {
      case e if e.isEmpty => Leaf(newLeaf)
      case xs    => Node(tree.rootLabel, headOption[LazyList[Tree[A]], Tree[A]].modify(_set(newLeaf))(xs) )
    }

    Lens(_get)(_set)
  }

  final def rightMostLabel[A]: Lens[Tree[A], A] = {
    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case e if e.isEmpty => tree.rootLabel
      case xs => _get(xs.last)
    }

    def _set(newLeaf: A)(tree: Tree[A]): Tree[A] = tree.subForest match {
      case e if e.isEmpty => Leaf(newLeaf)
      case xs    => Node(tree.rootLabel, lastOption[LazyList[Tree[A]], Tree[A]].modify(_set(newLeaf))(xs) )
    }

    Lens(_get)(_set)
  }

}
