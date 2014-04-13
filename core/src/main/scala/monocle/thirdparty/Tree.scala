package monocle.thirdparty

import monocle.SimpleLens
import monocle.function.Head._
import monocle.function.Last._
import monocle.syntax.traversal._
import scala.annotation.tailrec
import scala.collection.immutable.Stream.Empty
import scalaz.Tree

object tree extends TreeInstances

trait TreeInstances {

  def rootLabel[A]: SimpleLens[Tree[A], A] =
    SimpleLens[Tree[A], A](_.rootLabel, (tree, l) => Tree.node(l, tree.subForest))

  def subForest[A]: SimpleLens[Tree[A], Stream[Tree[A]]] =
    SimpleLens[Tree[A], Stream[Tree[A]]](_.subForest, (tree, children) => Tree.node(tree.rootLabel, children))

  def leftMostLabel[A]: SimpleLens[Tree[A], A] = {

    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case Empty    => tree.rootLabel
      case x #:: xs => _get(x)
    }

    def _set(tree: Tree[A], newLeaf: A): Tree[A] = tree.subForest match {
      case Empty => Tree.leaf(newLeaf)
      case xs    => Tree.node(tree.rootLabel, xs |->> head modify(_set(_, newLeaf)) )
    }

    SimpleLens[Tree[A], A](_get, _set)
  }

  def rightMostLabel[A]: SimpleLens[Tree[A], A] = {

    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case Empty => tree.rootLabel
      case xs    => _get(xs.last)
    }

    def _set(tree: Tree[A], newLeaf: A): Tree[A] = tree.subForest match {
      case Empty => Tree.leaf(newLeaf)
      case xs    => Tree.node(tree.rootLabel,  xs |->> last modify(_set(_, newLeaf)) )
    }

    SimpleLens[Tree[A], A](_get, _set)
  }



}
