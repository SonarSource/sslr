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

public class NextExpression implements ParsingExpression {

  private final ParsingExpression subExpression;

  public NextExpression(ParsingExpression subExpression) {
    this.subExpression = subExpression;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * <pre>
   * Choice L1
   * subExpression
   * BackCommit L2
   * L1: Fail
   * L2: ...
   * </pre>
   *
   * Should be noted that can be compiled without usage of instruction "BackCommit":
   * <pre>
   * Choice L1
   * Choice L2
   * subExpression
   * L2: Commit L3
   * L3: Fail
   * L1: ...
   * </pre>
   */
  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    Instruction[] sub = compiler.compile(subExpression);
    Instruction[] result = new Instruction[sub.length + 3];
    result[0] = Instruction.choice(result.length - 1);
    System.arraycopy(sub, 0, result, 1, sub.length);
    result[sub.length + 1] = Instruction.backCommit(2);
    result[sub.length + 2] = Instruction.backtrack();
    return result;
  }

  @Override
  public String toString() {
    return "Next[" + subExpression + "]";
  }

}
