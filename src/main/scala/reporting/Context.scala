/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package viper
package silicon
package reporting

import interfaces.state.{Store, Heap, State}
import interfaces.reporting.Context
import state.terms.Term

/* TODO: Use MultiSet[Member] instead of List[Member] */
case class DefaultContext(program: ast.Program,
                          visited: List[ast.Member] = Nil,
                          constrainableARPs: Set[Term] = Set(),
                          reserveHeaps: Stack[H] = Nil,
                          exhaleExt: Boolean = false,
//                          poldHeap: Option[H] = None,  /* Used to interpret e in PackageOld(e) */
                          lhsHeap: Option[H] = None, /* Used to interpret e in ApplyOld(e) */
                          additionalEvalHeap: Option[H] = None
//                          footprintHeap: Option[H] = None,
                          /*reinterpretWand: Boolean = true*/)
    extends Context[DefaultContext] {

  assert(!exhaleExt || reserveHeaps.size >= 3, "Invariant exhaleExt ==> reserveHeaps.size >= 3 violated")

  def incCycleCounter(m: ast.Member) = copy(visited = m :: visited)

  def decCycleCounter(m: ast.Member) = {
    require(visited.contains(m))

    val (ms, others) = visited.partition(_ == m)
    copy(visited = ms.tail ::: others)
  }

  def cycles(m: ast.Member) = visited.count(_ == m)

  def setConstrainable(arps: Seq[Term], constrainable: Boolean) = {
    val newConstrainableARPs =
      if (constrainable) constrainableARPs ++ arps
      else constrainableARPs -- arps

    copy(constrainableARPs = newConstrainableARPs)
  }
}
