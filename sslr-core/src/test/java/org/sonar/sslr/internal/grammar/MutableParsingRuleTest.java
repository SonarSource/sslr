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
package org.sonar.sslr.internal.grammar;

import com.sonar.sslr.api.AstNode;
import org.junit.Test;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MutableParsingRuleTest {

  @Test
  public void should_not_allow_redefinition() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    MutableParsingRule rule = new MutableParsingRule(ruleKey);
    rule.is(mock(ParsingExpression.class));
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> rule.is(mock(ParsingExpression.class)));
    assertEquals("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

  @Test
  public void should_override() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    MutableParsingRule rule = new MutableParsingRule(ruleKey);
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    rule.is(e1, e2);
    rule.override(e2);
    assertThat(rule.getExpression()).isSameAs(e2);
    rule.override(e1, e2);
    assertThat(rule.getExpression()).isInstanceOf(SequenceExpression.class);
  }

  @Test
  public void should_not_skip_from_AST() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    MutableParsingRule rule = new MutableParsingRule(ruleKey);
    AstNode astNode = mock(AstNode.class);
    assertThat(rule.hasToBeSkippedFromAst(astNode)).isFalse();
  }

  @Test
  public void should_skip_from_AST() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    MutableParsingRule rule = new MutableParsingRule(ruleKey);
    rule.skip();
    AstNode astNode = mock(AstNode.class);
    assertThat(rule.hasToBeSkippedFromAst(astNode)).isTrue();
  }

  @Test
  public void should_skip_from_AST_if_one_child() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    MutableParsingRule rule = new MutableParsingRule(ruleKey);
    rule.skipIfOneChild();
    AstNode astNode = mock(AstNode.class);
    when(astNode.getNumberOfChildren()).thenReturn(1);
    assertThat(rule.hasToBeSkippedFromAst(astNode)).isTrue();
  }

  @Test
  public void should_return_real_AstNodeType() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    MutableParsingRule rule = new MutableParsingRule(ruleKey);
    assertThat(rule.getRealAstNodeType()).isSameAs(ruleKey);
  }

}
