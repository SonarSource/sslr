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

import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.internal.matchers.Matcher;

public class TokenExpression implements Matcher, ParsingExpression {

  private final TokenType tokenType;
  private final ParsingExpression subExpression;

  public TokenExpression(TokenType tokenType, ParsingExpression subExpression) {
    this.tokenType = tokenType;
    this.subExpression = subExpression;
  }

  public TokenType getTokenType() {
    return tokenType;
  }

  /**
   * Compiles this expression into a sequence of instructions:
   * <pre>
   * Call L1
   * Jump L2
   * L1: subExpression
   * Return
   * L2: ...
   * </pre>
   */
  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    return compile(compiler, this, subExpression);
  }

  /**
   * Helper method to reduce duplication between {@link TokenExpression} and {@link TriviaExpression}.
   */
  static Instruction[] compile(CompilationHandler compiler, Matcher expression, ParsingExpression subExpression) {
    // TODO maybe can be optimized
    Instruction[] instr = compiler.compile(subExpression);
    Instruction[] result = new Instruction[instr.length + 4];
    result[0] = Instruction.call(2, expression);
    result[1] = Instruction.jump(instr.length + 3);
    result[2] = Instruction.ignoreErrors();
    System.arraycopy(instr, 0, result, 3, instr.length);
    result[3 + instr.length] = Instruction.ret();
    return result;
  }

  @Override
  public String toString() {
    return "Token " + tokenType + "[" + subExpression + "]";
  }

}
