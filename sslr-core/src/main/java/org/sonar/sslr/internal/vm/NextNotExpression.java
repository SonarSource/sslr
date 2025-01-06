/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
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

public class NextNotExpression implements ParsingExpression {

  private final ParsingExpression subExpression;

  public NextNotExpression(ParsingExpression subExpression) {
    this.subExpression = subExpression;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * <pre>
   * Choice L1
   * subExpression
   * FailTwice
   * L1: ...
   * </pre>
   *
   * Should be noted that can be compiled without usage of instruction "FailTwice":
   * <pre>
   * Choice L2
   * subExpression
   * Commit L1
   * L1: Fail
   * L2: ...
   * </pre>
   */
  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    Instruction[] sub = compiler.compile(subExpression);
    Instruction[] result = new Instruction[sub.length + 2];
    result[0] = Instruction.predicateChoice(sub.length + 2);
    System.arraycopy(sub, 0, result, 1, sub.length);
    result[sub.length + 1] = Instruction.failTwice();
    return result;
  }

  @Override
  public String toString() {
    return "NextNot[" + subExpression + "]";
  }

}
