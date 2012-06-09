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
package com.sonar.sslr.impl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;

public class ParsingStateTest {

  private ParsingState state;

  @Before
  public void init() {
    List<Token> tokens = new ArrayList<Token>();
    tokens.add(mockToken(MockTokenType.WORD, "java"));
    tokens.add(mockToken(MockTokenType.WORD, "public"));
    tokens.add(mockToken(MockTokenType.WORD, "class"));

    state = new ParsingState(tokens);
  }

  @Test
  public void testPopToken() {
    assertThat(state.popToken(getRuleMatcher("Dummy")).getValue()).isEqualTo("java");
    assertThat(state.popToken(getRuleMatcher("Dummy")).getValue()).isEqualTo("public");
  }

  @Test
  public void testPeekToken() {
    assertThat(state.peekToken(getRuleMatcher("Dummy")).getValue()).isEqualTo("java");
    assertThat(state.peekToken(getRuleMatcher("Dummy")).getValue()).isEqualTo("java");
  }

  @Test
  public void testGetIndex() {
    assertThat(state.lexerIndex).isEqualTo(0);
    state.popToken(getRuleMatcher("Dummy")).getValue();
    assertThat(state.lexerIndex).isEqualTo(1);
    state.peekToken(getRuleMatcher("Dummy")).getValue();
    assertThat(state.lexerIndex).isEqualTo(1);
  }

  @Test
  public void testHasNextToken() {
    assertThat(state.hasNextToken()).isTrue();
    state.popToken(getRuleMatcher("Dummy")).getValue();
    state.popToken(getRuleMatcher("Dummy")).getValue();
    state.popToken(getRuleMatcher("Dummy")).getValue();
    assertThat(state.hasNextToken()).isFalse();
  }

  @Test
  public void testGetOutpostMatcherOnPeek() {
    state.popToken(getRuleMatcher("Dummy1"));
    state.peekToken(getRuleMatcher("Dummy2"));
    assertThat(state.getOutpostMatcherToken().getValue()).isEqualTo("public");
  }

  @Test
  public void testGetOutpostMatcherOnPop() {
    state.popToken(getRuleMatcher("Dummy1"));
    state.popToken(getRuleMatcher("Dummy2"));
    assertThat(state.getOutpostMatcherToken().getValue()).isEqualTo("public");
  }

  @Test
  public void testHasMemoizedAst() {
    assertThat(state.hasMemoizedAst(getRuleMatcher("Dummy"))).isFalse();

    state.popToken(getRuleMatcher("Dummy"));
    RuleMatcher myrule = getRuleMatcher("MyRule");
    AstNode astNode = new AstNode(RuleDefinition.newRuleBuilder(myrule), "MyRule", null);
    astNode.setFromIndex(1);
    assertThat(astNode.getToIndex()).isEqualTo(0);

    state.memoizeAst(myrule, astNode);

    assertThat(astNode.getToIndex()).isEqualTo(1);
    state.popToken(getRuleMatcher("Dummy"));
    assertThat(state.hasMemoizedAst(myrule)).isFalse();
    assertThat(state.getMemoizedAst(myrule)).isNull();
  }

  @Test
  public void testGetMemoizedAst() {
    RuleMatcher myrule = getRuleMatcher("MyRule");
    state.popToken(myrule);
    state.popToken(myrule);
    AstNode astNode = new AstNode(RuleDefinition.newRuleBuilder(myrule), "MyRule", null);
    astNode.setFromIndex(0);
    state.memoizeAst(myrule, astNode);

    state.lexerIndex = 0;
    assertThat(state.hasMemoizedAst(myrule)).isTrue();
    assertThat(state.getMemoizedAst(myrule)).isNotNull();
    assertThat(state.lexerIndex).isEqualTo(0);
  }

  private static RuleMatcher getRuleMatcher(String ruleName) {
    return RuleDefinition.newRuleBuilder(ruleName).getRule();
  }
}
