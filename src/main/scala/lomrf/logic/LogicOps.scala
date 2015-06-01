/*
 * o                        o     o   o         o
 * |             o          |     |\ /|         | /
 * |    o-o o--o    o-o  oo |     | O |  oo o-o OO   o-o o   o
 * |    | | |  | | |    | | |     |   | | | |   | \  | |  \ /
 * O---oo-o o--O |  o-o o-o-o     o   o o-o-o   o  o o-o   o
 *             |
 *          o--o
 * o--o              o               o--o       o    o
 * |   |             |               |    o     |    |
 * O-Oo   oo o-o   o-O o-o o-O-o     O-o    o-o |  o-O o-o
 * |  \  | | |  | |  | | | | | |     |    | |-' | |  |  \
 * o   o o-o-o  o  o-o o-o o o o     o    | o-o o  o-o o-o
 *
 * Logical Markov Random Fields.
 *
 * Copyright (C) 2012 Anastasios Skarlatidis.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package lomrf.logic

import scala.collection.mutable

object LogicOps {

  implicit class FormulaOps(val formula: Formula) extends AnyVal {

    def contains(signature: AtomSignature): Boolean = formula match {
      case atom: AtomicFormula => atom.signature == signature
      case _ =>
        val queue = mutable.Queue[Formula]()
        formula.subFormulas.foreach(queue.enqueue(_))
        while (queue.nonEmpty) {
          val currentFormula = queue.dequeue()
          currentFormula match {
            case atom: AtomicFormula => if (atom.signature == signature) return true
            case _ => currentFormula.subFormulas.foreach(queue.enqueue(_))
          }
        }
        false
    }


    def first(signature: AtomSignature): Option[AtomicFormula] = formula match {
      case atom: AtomicFormula => if (atom.signature == signature) Some(atom) else None
      case _ =>
        val queue = mutable.Queue[Formula]()
        formula.subFormulas.foreach(queue.enqueue(_))
        while (queue.nonEmpty) {
          val currentFormula = queue.dequeue()
          currentFormula match {
            case atom: AtomicFormula => if (atom.signature == signature) return Some(atom)
            case _ => currentFormula.subFormulas.foreach(f => queue.enqueue(f))
          }
        }
        None
    }

    def all(signature: AtomSignature): Seq[AtomicFormula] = formula match {
      case atom: AtomicFormula => if (atom.signature == signature) Seq(atom) else Seq()
      case _ =>
        val queue = mutable.Queue[Formula]()
        formula.subFormulas.foreach(queue.enqueue(_))
        var result = Vector[AtomicFormula]()

        while (queue.nonEmpty) {
          val currentFormula = queue.dequeue()
          currentFormula match {
            case atom: AtomicFormula =>
              if (atom.signature == signature) result :+= atom

            case _ => currentFormula.subFormulas.foreach(f => queue.enqueue(f))
          }
        }
        result
    }

    def signatures: Set[AtomSignature] = formula match {
      case atom: AtomicFormula => Set(atom.signature)
      case _ =>
        val queue = mutable.Queue[Formula]()
        formula.subFormulas.foreach(queue.enqueue(_))
        var result = Set[AtomSignature]()

        while (queue.nonEmpty) {
          val currentFormula = queue.dequeue()
          currentFormula match {
            case atom: AtomicFormula => result += atom.signature
            case _ => currentFormula.subFormulas.foreach(queue.enqueue(_))
          }
        }

        result
    }


    def replace(targetAtom: AtomicFormula, replacement: Formula): Option[Formula] = {

      def doReplace(inFormula: Formula, withFormula: Formula): Formula ={
        inFormula match {
          case f: AtomicFormula => if (f.signature == targetAtom.signature) withFormula else f

          case f: WeightedFormula => WeightedFormula(f.weight, doReplace(f.formula, withFormula))

          case f: Not => Not(doReplace(f.arg, withFormula))

          case f: And => And(doReplace(f.left, withFormula), doReplace(f.right, withFormula))

          case f: Or => Or(doReplace(f.left, withFormula), doReplace(f.right, withFormula))

          case f: UniversalQuantifier => UniversalQuantifier(f.variable, doReplace(f.formula, withFormula))

          case f: ExistentialQuantifier => ExistentialQuantifier(f.variable, doReplace(f.formula, withFormula))

          case f: Equivalence => Equivalence(doReplace(f.left, withFormula), doReplace(f.right, withFormula))

          case f: Implies => Implies(doReplace(f.left, withFormula), doReplace(f.right, withFormula))

          case _ => throw new IllegalStateException("Illegal formula type.")
        }
      }

      Unify(targetAtom, formula) match {
        case Some(theta) if theta.nonEmpty =>

          val replacementPrime = Substitute(theta, replacement)
          val targetPrime = Substitute(theta, formula)
          val result = doReplace(targetPrime, replacementPrime)

          Some(result)
        case _ => None // nothing to unify
      }
    }


  }

}