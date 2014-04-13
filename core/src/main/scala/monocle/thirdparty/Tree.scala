package monocle.thirdparty

import monocle.SimpleLens
import monocle.function.Head._
import monocle.function.Last._
import monocle.std.option.some
import monocle.syntax.lens._
import scala.annotation.tailrec
import scala.collection.immutable.Stream.Empty
import scalaz.Tree


object tree extends TreeInstances

trait TreeInstances {

  def leftMostNode[A]: SimpleLens[Tree[A], A] = {

    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case Empty    => tree.rootLabel
      case x #:: xs => _get(x)
    }

    def _set(tree: Tree[A], newLeaf: A): Tree[A] = tree.subForest match {
      case Empty => Tree.leaf(newLeaf)
      case xs    => Tree.node(tree.rootLabel, xs |-> head |->> some modify(_set(_, newLeaf)) )
    }

    SimpleLens[Tree[A], A](_get, _set)
  }

  def rightMostNode[A]: SimpleLens[Tree[A], A] = {

    @tailrec
    def _get(tree: Tree[A]): A = tree.subForest match {
      case Empty => tree.rootLabel
      case xs    => _get(xs.last)
    }

    def _set(tree: Tree[A], newLeaf: A): Tree[A] = tree.subForest match {
      case Empty => Tree.leaf(newLeaf)
      case xs    => Tree.node(tree.rootLabel,  xs |-> last |->> some modify(_set(_, newLeaf)) )
    }

    SimpleLens[Tree[A], A](_get, _set)
  }



}
