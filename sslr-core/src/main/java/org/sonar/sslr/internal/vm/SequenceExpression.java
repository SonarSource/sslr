/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.internal.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequenceExpression implements ParsingExpression {

  private final ParsingExpression[] subExpressions;

  public SequenceExpression(ParsingExpression... subExpressions) {
    this.subExpressions = subExpressions;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * <pre>
   * subExpressions[0]
   * subExpressions[1]
   * subExpressions[2]
   * ...
   * </pre>
   */
  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    List<Instruction> result = new ArrayList<>();
    for (ParsingExpression subExpression : subExpressions) {
      Instruction.addAll(result, compiler.compile(subExpression));
    }
    return result.toArray(new Instruction[result.size()]);
  }

  @Override
  public String toString() {
    return "Sequence" + Arrays.toString(subExpressions);
  }

}
