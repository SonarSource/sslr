/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.vm;

import com.sonar.sslr.api.AstNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParsingRuleTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_not_allow_redefinition() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    ParsingRule rule = new ParsingRule(VmGrammarBuilder.create(), ruleKey);
    rule.is(mock(ParsingExpression.class));
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    rule.is(mock(ParsingExpression.class));
  }

  @Test
  public void should_override() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    ParsingRule rule = new ParsingRule(VmGrammarBuilder.create(), ruleKey);
    ParsingExpression e1 = mock(ParsingExpression.class);
    rule.is(e1);
    ParsingExpression e2 = mock(ParsingExpression.class);
    rule.override(e2);
    assertThat(rule.getExpression()).isSameAs(e2);
    rule.override(e1, e2);
    assertThat(rule.getExpression()).isInstanceOf(SequenceExpression.class);
  }

  @Test
  public void recovery_rule_not_supported() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    ParsingRule rule = new ParsingRule(VmGrammarBuilder.create(), ruleKey);
    thrown.expect(UnsupportedOperationException.class);
    rule.recoveryRule();
  }

  @Test
  public void should_not_skip_from_AST() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    ParsingRule rule = new ParsingRule(VmGrammarBuilder.create(), ruleKey);
    AstNode astNode = mock(AstNode.class);
    assertThat(rule.convert().hasToBeSkippedFromAst(astNode)).isFalse();
  }

  @Test
  public void should_skip_from_AST() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    ParsingRule rule = new ParsingRule(VmGrammarBuilder.create(), ruleKey);
    rule.skip();
    AstNode astNode = mock(AstNode.class);
    assertThat(rule.convert().hasToBeSkippedFromAst(astNode)).isTrue();
  }

  @Test
  public void should_skip_from_AST_if_one_child() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    ParsingRule rule = new ParsingRule(VmGrammarBuilder.create(), ruleKey);
    rule.skipIfOneChild();
    AstNode astNode = mock(AstNode.class);
    when(astNode.getNumberOfChildren()).thenReturn(1);
    assertThat(rule.convert().hasToBeSkippedFromAst(astNode)).isTrue();
  }

  @Test
  public void should_return_real_AstNodeType() {
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    ParsingRule rule = new ParsingRule(VmGrammarBuilder.create(), ruleKey);
    assertThat(rule.convert().getRealAstNodeType()).isSameAs(ruleKey);
  }

}
