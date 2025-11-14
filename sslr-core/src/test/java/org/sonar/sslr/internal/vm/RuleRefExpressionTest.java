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
import org.sonar.sslr.grammar.GrammarRuleKey;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class RuleRefExpressionTest {

  private GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
  private RuleRefExpression expression = new RuleRefExpression(ruleKey);
  private Machine machine = mock(Machine.class);

  @Test
  public void should_compile() {
    assertThat(expression.compile(new CompilationHandler())).containsOnly(expression);
    assertThat(expression.getRuleKey()).isSameAs(ruleKey);
    assertThat(expression.toString()).isEqualTo("Ref " + ruleKey);
  }

  @Test
  public void can_not_be_executed() {
    assertThrows(UnsupportedOperationException.class,
      () -> expression.execute(machine));
  }

}
