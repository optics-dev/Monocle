package monocle.scalaz

import _root_.scalaz.Tree
import monocle.function._
import monocle.std.stream._
import monocle.syntax._
import monocle.{SimpleIso, SimpleLens}
import scala.annotation.tailrec
import scala.collection.immutable.Stream.Empty


object tree extends TreeFunctions with TreeInstances

trait TreeFunctions {

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
      case xs    => Tree.node(tree.rootLabel, xs ^|->> headOption modify(_set(_, newLeaf)) )
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
      case xs    => Tree.node(tree.rootLabel,  xs ^|->> lastOption modify(_set(_, newLeaf)) )
    }

    SimpleLens[Tree[A], A](_get, _set)
  }

}

trait TreeInstances {

  implicit def treeEach[A]: Each[Tree[A], A] = Each.traverseEach[Tree, A]

  implicit def treeReverse[A]: Reverse[Tree[A], Tree[A]] = new Reverse[Tree[A], Tree[A]] {
    def reverse = SimpleIso[Tree[A], Tree[A]](reverseTree, reverseTree)
    private def reverseTree(tree: Tree[A]): Tree[A] = Tree.node(tree.rootLabel, tree.subForest.reverse.map(reverseTree))
  }

}
