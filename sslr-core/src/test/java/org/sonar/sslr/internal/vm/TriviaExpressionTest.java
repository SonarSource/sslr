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
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TriviaExpressionTest {

  @Test
  public void should_compile() {
    TriviaExpression expression = new TriviaExpression(TriviaKind.COMMENT, new SubExpression(1, 2));
    assertThat(expression.toString()).isEqualTo("Trivia COMMENT[SubExpression]");
    Instruction[] instructions = expression.compile(new CompilationHandler());
    assertThat(instructions).isEqualTo(new Instruction[] {
      Instruction.call(2, expression),
      Instruction.jump(5),
      Instruction.ignoreErrors(),
      SubExpression.mockInstruction(1),
      SubExpression.mockInstruction(2),
      Instruction.ret()
    });
  }

  @Test
  public void should_implement_Matcher() {
    TriviaExpression expression = new TriviaExpression(TriviaKind.COMMENT, mock(ParsingExpression.class));
    // Important for AstCreator
    assertThat(expression.getTriviaKind()).isSameAs(TriviaKind.COMMENT);
  }

}
