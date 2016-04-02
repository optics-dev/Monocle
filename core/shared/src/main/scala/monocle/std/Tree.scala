package monocle.std

import monocle.function._
import monocle.{Iso, Lens, Traversal}
import monocle.function.all._
import monocle.std.stream._

import scalaz.Tree.{Leaf, Node}
import scalaz.{Applicative, Traverse, Tree}
import scalaz.std.stream._
import scala.annotation.tailrec
import scala.collection.immutable.Stream.Empty

object tree extends TreeOptics

trait TreeOptics {

  final def rootLabel[A]: Lens[Tree[A], A] =
    Lens[Tree[A], A](_.rootLabel)(l => tree => Node(l, tree.subForest))

  final def subForest[A]: Lens[Tree[A], Stream[Tree[A]]] =
    Lens[Tree[A], Stream[Tree[A]]](_.subForest)(children => tree => Node(tree.rootLabel, children))

  final def leftMostLabel[A]: Lens[Tree[A], A] = {
    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case Empty    => tree.rootLabel
      case x #:: xs => _get(x)
    }

    def _set(newLeaf: A)(tree: Tree[A]): Tree[A] = tree.subForest match {
      case Empty => Leaf(newLeaf)
      case xs    => Node(tree.rootLabel, headOption[Stream[Tree[A]], Tree[A]].modify(_set(newLeaf))(xs) )
    }

    Lens(_get)(_set)
  }

  final def rightMostLabel[A]: Lens[Tree[A], A] = {
    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case Empty => tree.rootLabel
      case xs    => _get(xs.last)
    }

    def _set(newLeaf: A)(tree: Tree[A]): Tree[A] = tree.subForest match {
      case Empty => Leaf(newLeaf)
      case xs    => Node(tree.rootLabel, lastOption[Stream[Tree[A]], Tree[A]].modify(_set(newLeaf))(xs) )
    }

    Lens(_get)(_set)
  }

  implicit def treeEach[A]: Each[Tree[A], A] = Each.traverseEach[Tree, A]

  implicit def treeReverse[A]: Reverse[Tree[A], Tree[A]] = new Reverse[Tree[A], Tree[A]] {
    def reverse = Iso[Tree[A], Tree[A]](reverseTree)(reverseTree)
    private def reverseTree(tree: Tree[A]): Tree[A] = Node(tree.rootLabel, tree.subForest.reverse.map(reverseTree))
  }

  implicit def treePlated[A]: Plated[Tree[A]] = new Plated[Tree[A]] {
    val plate: Traversal[Tree[A], Tree[A]] = new Traversal[Tree[A], Tree[A]] {
      def modifyF[F[_]: Applicative](f: Tree[A] => F[Tree[A]])(s: Tree[A]): F[Tree[A]] =
        Applicative[F].map(Traverse[Stream].traverse(s.subForest)(f))(Node(s.rootLabel, _))
    }
  }

}
