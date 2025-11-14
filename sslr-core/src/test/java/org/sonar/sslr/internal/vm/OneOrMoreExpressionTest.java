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

public class OneOrMoreExpressionTest {

  @Test
  public void should_compile() {
    OneOrMoreExpression expression = new OneOrMoreExpression(new SubExpression(1, 2));
    assertThat(expression.toString()).isEqualTo("OneOrMore[SubExpression]");
    Instruction[] instructions = expression.compile(new CompilationHandler());
    assertThat(instructions).isEqualTo(new Instruction[] {
      Instruction.choice(6),
      SubExpression.mockInstruction(1),
      SubExpression.mockInstruction(2),
      Instruction.commitVerify(1),
      Instruction.choice(3),
      Instruction.jump(-4),
      Instruction.backtrack()
    });
  }

}
