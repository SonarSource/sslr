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

import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.matchers.Matcher;

public class TriviaExpression implements Matcher, ParsingExpression {

  private final TriviaKind triviaKind;
  private final ParsingExpression subExpression;

  public TriviaExpression(TriviaKind triviaKind, ParsingExpression subExpression) {
    this.triviaKind = triviaKind;
    this.subExpression = subExpression;
  }

  public TriviaKind getTriviaKind() {
    return triviaKind;
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
    return TokenExpression.compile(compiler, this, subExpression);
  }

  @Override
  public String toString() {
    return "Trivia " + triviaKind + "[" + subExpression + "]";
  }

}
