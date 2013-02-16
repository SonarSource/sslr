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

import com.google.common.collect.Lists;

import java.util.List;

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
  public Instruction[] compile() {
    int[] offsets = new int[subExpressions.length - 1];
    List<Instruction> result = Lists.newArrayList();
    for (int i = 0; i < subExpressions.length - 1; i++) {
      // add placeholder for "Choice"
      result.add(null);
      // add program
      Instruction.addAll(result, subExpressions[i].compile());
      // add placeholder for "Commit"
      result.add(null);
      offsets[i] = result.size();
    }
    // add last program
    Instruction.addAll(result, subExpressions[subExpressions.length - 1].compile());

    // replace placholders
    int index = 0;
    for (int i = 0; i < subExpressions.length - 1; i++) {
      while (result.get(index) != null) {
        index++;
      }
      result.set(index, Instruction.choice(offsets[i] - index));
      while (result.get(index) != null) {
        index++;
      }
      result.set(index, Instruction.commit(result.size() - index));
    }

    return result.toArray(new Instruction[result.size()]);
  }

}
