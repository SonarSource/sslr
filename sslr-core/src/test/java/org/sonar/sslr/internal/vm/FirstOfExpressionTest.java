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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class FirstOfExpressionTest {

  @Test
  public void should_compile() {
    FirstOfExpression expression = new FirstOfExpression(
        new SubExpression(1, 2, 3),
        new SubExpression(4, 5),
        new SubExpression(6));
    assertThat(expression.toString()).isEqualTo("FirstOf[SubExpression, SubExpression, SubExpression]");
    Instruction[] instructions = expression.compile(new CompilationHandler());
    assertThat(instructions).isEqualTo(new Instruction[] {
      Instruction.choice(5),
      SubExpression.mockInstruction(1),
      SubExpression.mockInstruction(2),
      SubExpression.mockInstruction(3),
      Instruction.commit(6),
      Instruction.choice(4),
      SubExpression.mockInstruction(4),
      SubExpression.mockInstruction(5),
      Instruction.commit(2),
      SubExpression.mockInstruction(6),
    });
  }

}
