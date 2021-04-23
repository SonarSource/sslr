/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.grammar;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.sslr.grammar.GrammarBuilder.RuleBuilder;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RuleBuilderTest {

  private GrammarBuilder b = mock(GrammarBuilder.class);
  private CompilableGrammarRule delegate = mock(CompilableGrammarRule.class);
  private RuleBuilder ruleBuilder = new RuleBuilder(b, delegate);

  @Test
  public void test_is() {
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    when(b.convertToExpression(e1)).thenReturn(e2);
    ruleBuilder.is(e1);
    verify(delegate).setExpression(e2);
  }

  @Test
  public void test_is2() {
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    ParsingExpression e3 = mock(ParsingExpression.class);
    when(b.convertToExpression(Mockito.any(SequenceExpression.class))).thenReturn(e3);
    ruleBuilder.is(e1, e2);
    verify(delegate).setExpression(e3);
  }

  @Test
  public void should_fail_to_redefine() {
    ParsingExpression e = mock(ParsingExpression.class);
    when(delegate.getExpression()).thenReturn(e);
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    when(delegate.getRuleKey()).thenReturn(ruleKey);
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> ruleBuilder.is(e));
    assertEquals("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

  @Test
  public void test_override() {
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    ParsingExpression e3 = mock(ParsingExpression.class);
    when(b.convertToExpression(e1)).thenReturn(e1);
    when(b.convertToExpression(e2)).thenReturn(e3);
    ruleBuilder.is(e1);
    ruleBuilder.override(e2);
    InOrder inOrder = Mockito.inOrder(delegate);
    inOrder.verify(delegate).setExpression(e1);
    inOrder.verify(delegate).setExpression(e3);
  }

  @Test
  public void test_override2() {
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    ParsingExpression e3 = mock(ParsingExpression.class);
    when(b.convertToExpression(e1)).thenReturn(e1);
    ruleBuilder.is(e1);
    verify(delegate).setExpression(e1);
    when(b.convertToExpression(Mockito.any(SequenceExpression.class))).thenReturn(e3);
    ruleBuilder.override(e1, e2);
    verify(delegate).setExpression(e3);
  }

  @Test
  public void test_skip() {
    ruleBuilder.skip();
    verify(delegate).skip();
  }

  @Test
  public void test_skipIfOneChild() {
    ruleBuilder.skipIfOneChild();
    verify(delegate).skipIfOneChild();
  }

}
