/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.vm;

public class OneOrMoreExpression implements ParsingExpression {

  private final ParsingExpression subExpression;

  public OneOrMoreExpression(ParsingExpression subExpression) {
    this.subExpression = subExpression;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * <pre>
   * Choice L3
   * L1: subExpression
   * CommitVerify L2
   * L2: Choice L4
   * Jump L1
   * L3: Fail
   * L4: ....
   * </pre>
   *
   * Should be noted that can be compiled with help of {@link ZeroOrMoreExpression}:
   * <pre>
   * subExpresson
   * L1: Choice L2
   * subExpression
   * CommitVerify L1
   * L2: ...
   * </pre>
   */
  public Instruction[] compile() {
    Instruction[] sub = subExpression.compile();
    Instruction[] result = new Instruction[sub.length + 5];
    result[0] = Instruction.choice(sub.length + 4);
    System.arraycopy(sub, 0, result, 1, sub.length);
    result[sub.length + 1] = Instruction.commitVerify(1);
    result[sub.length + 2] = Instruction.choice(3);
    result[sub.length + 3] = Instruction.jump(-2 - sub.length);
    result[sub.length + 4] = Instruction.backtrack();
    return result;
  }

}
