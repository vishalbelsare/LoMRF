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
 * Copyright (c) Anastasios Skarlatidis.
 *
 * This file is part of Logical Markov Random Fields (LoMRF).
 *
 * LoMRF is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * LoMRF is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with LoMRF. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package lomrf.logic

/**
  * Types and constants can be declared in a .kb file having the following syntax:
  * {{{
  *  <typename> =  { <constant1>, <constant2>, ... },
  * }}}
  * @example {{{
  *           person =  { Alice, Bob }
  *           time = {1, ..., 100} // for quickly defining a range of integers
  *          }}}
  * According to the above example definitions, the domain '''person''' is composed of
  * 2 constant symbols (i.e. Alice and Bob). Similarly the domain  '''time''' is composed
  * of the symbols that belong into the range [1, 100].
  *
  * @param name a name for the MLN definition
  */
sealed abstract class MLNTypeDefinition(name: String) extends MLNDomainExpression {

  /**
    * @return the name of the MLN definition
    */
  def getName: String = name
}

/**
  * Integer type definition is defined by a name and an interval of integer literals.
  *
  * @param name a name for the MLN definition
  * @param from the beginning of the interval
  * @param to the end of the interval
  */
case class IntegerTypeDefinition(name: String, from: Int, to: Int) extends MLNTypeDefinition(name)

/**
  * Constant type definition is defined by a name and a sequence of constants.
  *
  * @param name a name for the MLN definition
  * @param constants a sequence of constants
  */
case class ConstantTypeDefinition(name: String, constants: Seq[String]) extends MLNTypeDefinition(name)


