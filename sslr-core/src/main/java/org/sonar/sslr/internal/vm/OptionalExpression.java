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

public class OptionalExpression implements ParsingExpression {

  private final ParsingExpression subExpression;

  public OptionalExpression(ParsingExpression subExpression) {
    this.subExpression = subExpression;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * </pre>
   * Choice L1
   * subExpression
   * Commit L1
   * L1: ...
   * </pre>
   */
  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    // not described in paper
    Instruction[] instr = compiler.compile(subExpression);
    Instruction[] result = new Instruction[instr.length + 2];
    result[0] = Instruction.choice(result.length);
    System.arraycopy(instr, 0, result, 1, instr.length);
    result[instr.length + 1] = Instruction.commit(1);
    return result;
  }

  @Override
  public String toString() {
    return "Optional[" + subExpression + "]";
  }

}
