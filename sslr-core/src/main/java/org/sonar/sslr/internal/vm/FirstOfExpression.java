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

import java.util.Arrays;

public class FirstOfExpression implements ParsingExpression {

  private final ParsingExpression[] subExpressions;

  public FirstOfExpression(ParsingExpression... subExpressions) {
    this.subExpressions = subExpressions;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * <pre>
   * Choice L1
   * subExpression[0]
   * Commit E
   * L1: Choice L2
   * subExpression[1]
   * Commit E
   * L2: Choice L3
   * subExpression[2]
   * Commit E
   * L3: subExpression[3]
   * E: ...
   * </pre>
   */
  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    int index = 0;
    Instruction[][] sub = new Instruction[subExpressions.length][];
    for (int i = 0; i < subExpressions.length; i++) {
      sub[i] = compiler.compile(subExpressions[i]);
      index += sub[i].length;
    }
    Instruction[] result = new Instruction[index + (subExpressions.length - 1) * 2];

    index = 0;
    for (int i = 0; i < subExpressions.length - 1; i++) {
      result[index] = Instruction.choice(sub[i].length + 2);
      System.arraycopy(sub[i], 0, result, index + 1, sub[i].length);
      index += sub[i].length + 1;
      result[index] = Instruction.commit(result.length - index);
      index++;
    }
    System.arraycopy(sub[sub.length - 1], 0, result, index, sub[sub.length - 1].length);

    return result;
  }

  @Override
  public String toString() {
    return "FirstOf" + Arrays.toString(subExpressions);
  }

}
