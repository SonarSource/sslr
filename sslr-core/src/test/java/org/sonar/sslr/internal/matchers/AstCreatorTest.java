/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
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
package org.sonar.sslr.internal.matchers;

import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.parser.ParsingResult;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AstCreatorTest {

  @Test
  public void should_create_tokens_and_trivias() {
    char[] input = "foo bar".toCharArray();

    TokenExpression tokenMatcher = mockTokenMatcher(GenericTokenType.IDENTIFIER);
    TokenExpression triviaMatcher = mockTokenMatcher(GenericTokenType.COMMENT);
    MutableParsingRule ruleMatcher = mockRuleMatcher("rule");
    AstNodeType realAstNodeType = mock(AstNodeType.class);
    when(ruleMatcher.getRealAstNodeType()).thenReturn(realAstNodeType);

    ParseNode triviaNode = new ParseNode(0, 4, Collections.<ParseNode>emptyList(), triviaMatcher);
    ParseNode tokenNode = new ParseNode(4, 7, Collections.<ParseNode>emptyList(), tokenMatcher);
    ParseNode parseTreeRoot = new ParseNode(0, 7, ImmutableList.of(triviaNode, tokenNode), ruleMatcher);

    InputBuffer inputBuffer = new ImmutableInputBuffer(input);
    ParsingResult parsingResult = new ParsingResult(inputBuffer, true, parseTreeRoot, null);

    AstNode astNode = AstCreator.create(parsingResult, new LocatedText(null, input));
    System.out.println(AstXmlPrinter.print(astNode));

    assertThat(astNode.getType()).isSameAs(realAstNodeType);
    assertThat(astNode.getName()).isEqualTo("rule");
    assertThat(astNode.getFromIndex()).isEqualTo(0);
    assertThat(astNode.getToIndex()).isEqualTo(7);
    assertThat(astNode.hasChildren()).isTrue();

    assertThat(astNode.getTokens()).hasSize(1);
    Token token = astNode.getTokens().get(0);
    assertThat(astNode.getToken()).isSameAs(token);
    assertThat(token.getValue()).isEqualTo("bar");
    assertThat(token.getOriginalValue()).isEqualTo("bar");
    assertThat(token.getLine()).isEqualTo(1);
    assertThat(token.getColumn()).isEqualTo(4);
    assertThat(token.getType()).isEqualTo(GenericTokenType.IDENTIFIER);

    assertThat(token.getTrivia()).hasSize(1);
    Trivia trivia = token.getTrivia().get(0);
    Token triviaToken = trivia.getToken();
    assertThat(triviaToken.getValue()).isEqualTo("foo ");
    assertThat(triviaToken.getOriginalValue()).isEqualTo("foo ");
    assertThat(triviaToken.getLine()).isEqualTo(1);
    assertThat(triviaToken.getColumn()).isEqualTo(0);
    assertThat(triviaToken.getType()).isEqualTo(GenericTokenType.COMMENT);
  }

  @Test
  public void should_create_tokens_without_TokenMatcher() {
    char[] input = "foobar".toCharArray();

    ParseNode firstTerminal = new ParseNode(0, 3, Collections.<ParseNode>emptyList(), null);
    ParseNode secondTerminal = new ParseNode(3, 6, Collections.<ParseNode>emptyList(), null);
    MutableParsingRule ruleMatcher = mockRuleMatcher("rule");
    AstNodeType realAstNodeType = mock(AstNodeType.class);
    when(ruleMatcher.getRealAstNodeType()).thenReturn(realAstNodeType);
    ParseNode parseTreeRoot = new ParseNode(0, 6, ImmutableList.of(firstTerminal, secondTerminal), ruleMatcher);

    InputBuffer inputBuffer = new ImmutableInputBuffer(input);
    ParsingResult parsingResult = new ParsingResult(inputBuffer, true, parseTreeRoot, null);

    AstNode astNode = AstCreator.create(parsingResult, new LocatedText(null, input));
    System.out.println(AstXmlPrinter.print(astNode));

    assertThat(astNode.getType()).isSameAs(realAstNodeType);
    assertThat(astNode.getName()).isEqualTo("rule");
    assertThat(astNode.getFromIndex()).isEqualTo(0);
    assertThat(astNode.getToIndex()).isEqualTo(6);
    assertThat(astNode.hasChildren()).isTrue();

    assertThat(astNode.getTokens()).hasSize(2);
    Token token = astNode.getTokens().get(0);
    assertThat(astNode.getToken()).isSameAs(token);
    assertThat(token.getValue()).isEqualTo("foo");
    assertThat(token.getOriginalValue()).isEqualTo("foo");
    assertThat(token.getLine()).isEqualTo(1);
    assertThat(token.getColumn()).isEqualTo(0);
    assertThat(token.getType()).isSameAs(AstCreator.UNDEFINED_TOKEN_TYPE);
    assertThat(token.getType().getName()).isEqualTo("TOKEN");

    token = astNode.getTokens().get(1);
    assertThat(token.getValue()).isEqualTo("bar");
    assertThat(token.getOriginalValue()).isEqualTo("bar");
    assertThat(token.getLine()).isEqualTo(1);
    assertThat(token.getColumn()).isEqualTo(3);
    assertThat(token.getType()).isSameAs(AstCreator.UNDEFINED_TOKEN_TYPE);
  }

  @Test
  public void should_skip_nodes() {
    char[] input = "foo".toCharArray();

    MutableParsingRule ruleMatcher1 = mockRuleMatcher("rule1");
    when(ruleMatcher1.hasToBeSkippedFromAst(Mockito.any(AstNode.class))).thenReturn(true);
    MutableParsingRule ruleMatcher2 = mockRuleMatcher("rule2");
    AstNodeType realAstNodeType = mock(AstNodeType.class);
    when(ruleMatcher2.getRealAstNodeType()).thenReturn(realAstNodeType);

    ParseNode node = new ParseNode(0, 3, Collections.<ParseNode>emptyList(), ruleMatcher1);
    ParseNode parseTreeRoot = new ParseNode(0, 3, ImmutableList.of(node), ruleMatcher2);

    InputBuffer inputBuffer = new ImmutableInputBuffer(input);
    ParsingResult parsingResult = new ParsingResult(inputBuffer, true, parseTreeRoot, null);

    AstNode astNode = AstCreator.create(parsingResult, new LocatedText(null, input));
    System.out.println(AstXmlPrinter.print(astNode));

    assertThat(astNode.getType()).isSameAs(realAstNodeType);
    assertThat(astNode.getName()).isEqualTo("rule2");
    assertThat(astNode.getFromIndex()).isEqualTo(0);
    assertThat(astNode.getToIndex()).isEqualTo(3);
    assertThat(astNode.hasChildren()).isFalse();
    assertThat(astNode.getToken()).isNull();
  }

  private static MutableParsingRule mockRuleMatcher(String name) {
    return when(mock(MutableParsingRule.class).getName()).thenReturn(name).getMock();
  }

  private static TokenExpression mockTokenMatcher(TokenType tokenType) {
    return when(mock(TokenExpression.class).getTokenType()).thenReturn(tokenType).getMock();
  }

}
