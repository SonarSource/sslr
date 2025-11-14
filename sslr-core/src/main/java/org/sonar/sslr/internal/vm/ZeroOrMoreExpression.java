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

public class ZeroOrMoreExpression implements ParsingExpression {

  private final ParsingExpression subExpression;

  public ZeroOrMoreExpression(ParsingExpression subExpression) {
    this.subExpression = subExpression;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * <pre>
   * L1: Choice L2
   * subExpression
   * CommitVerify L1
   * L2: ...
   * </pre>
   */
  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    // TODO maybe can be optimized by introduction of new instruction PartialCommit
    Instruction[] sub = compiler.compile(subExpression);
    Instruction[] result = new Instruction[sub.length + 2];
    result[0] = Instruction.choice(sub.length + 2);
    System.arraycopy(sub, 0, result, 1, sub.length);
    result[sub.length + 1] = Instruction.commitVerify(-1 - sub.length);
    return result;
  }

  @Override
  public String toString() {
    return "ZeroOrMore[" + subExpression + "]";
  }

}
