package monocle.std

import monocle.Lens
import monocle.function.all._

import scala.annotation.tailrec
import scalaz.{EphemeralStream, Tree}
import scalaz.Tree.{Leaf, Node}
import EphemeralStream.##::

object tree extends TreeOptics

trait TreeOptics {

  final def rootLabel[A]: Lens[Tree[A], A] =
    Lens[Tree[A], A](_.rootLabel)(l => tree => Node(l, tree.subForest))

  final def subForest[A]: Lens[Tree[A], EphemeralStream[Tree[A]]] =
    Lens[Tree[A], EphemeralStream[Tree[A]]](_.subForest)(children => tree => Node(tree.rootLabel, children))

  final def leftMostLabel[A]: Lens[Tree[A], A] = {
    @tailrec
    def _get(tree: Tree[A]): A = {
      tree.subForest match {
        case x ##:: _ => _get(x)
        case _ => tree.rootLabel
      }
    }

    def _set(newLeaf: A)(tree: Tree[A]): Tree[A] = {
      if(tree.subForest.isEmpty) {
        Leaf(newLeaf)
      } else {
        Node(tree.rootLabel, headOption[EphemeralStream[Tree[A]], Tree[A]].modify(_set(newLeaf))(tree.subForest) )
      }
    }

    Lens(_get)(_set)
  }

  final def rightMostLabel[A]: Lens[Tree[A], A] = {
    @tailrec
    def _get(tree: Tree[A]): A = {
      if(tree.subForest.isEmpty) {
        tree.rootLabel
      } else {
        _get(tree.subForest.reverse.headOption.get)
      }
    }

    def _set(newLeaf: A)(tree: Tree[A]): Tree[A] = {
      if(tree.subForest.isEmpty) {
        Leaf(newLeaf)
      } else {
        Node(tree.rootLabel, lastOption[EphemeralStream[Tree[A]], Tree[A]].modify(_set(newLeaf))(tree.subForest) )
      }
    }

    Lens(_get)(_set)
  }

}
