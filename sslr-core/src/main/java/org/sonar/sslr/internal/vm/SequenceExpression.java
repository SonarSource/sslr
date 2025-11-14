/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
