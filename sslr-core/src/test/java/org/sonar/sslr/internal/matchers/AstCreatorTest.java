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
package org.sonar.sslr.internal.matchers;

import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.sslr.matchers.InputBuffer;
import org.sonar.sslr.matchers.ParsingResult;

import java.net.URI;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AstCreatorTest {

  private URI uri;

  @Before
  public void setUp() throws Exception {
    uri = new URI("tests://unittest");
  }

  @Test
  public void should_create_tokens_and_trivias() {
    char[] input = "foo bar".toCharArray();

    TokenMatcher tokenMatcher = mockTokenMatcher(GenericTokenType.IDENTIFIER);
    TokenMatcher triviaMatcher = mockTokenMatcher(GenericTokenType.COMMENT);
    GrammarElementMatcher ruleMatcher = mockRuleMatcher("rule");

    ParseNode triviaNode = new ParseNode(0, 4, Collections.EMPTY_LIST, triviaMatcher);
    ParseNode tokenNode = new ParseNode(4, 7, Collections.EMPTY_LIST, tokenMatcher);
    ParseNode parseTreeRoot = new ParseNode(0, 7, ImmutableList.of(triviaNode, tokenNode), ruleMatcher);

    InputBuffer inputBuffer = new ImmutableInputBuffer(input);
    ParsingResult parsingResult = new ParsingResult(inputBuffer, true, parseTreeRoot, null);

    AstNode astNode = AstCreator.create(uri, parsingResult);
    System.out.println(AstXmlPrinter.print(astNode));

    assertThat(astNode.getType()).isSameAs(ruleMatcher);
    assertThat(astNode.getName()).isEqualTo("rule");
    assertThat(astNode.getFromIndex()).isEqualTo(0);
    assertThat(astNode.getToIndex()).isEqualTo(7);
    assertThat(astNode.hasChildren()).isTrue();

    assertThat(astNode.getTokens()).hasSize(1);
    Token token = astNode.getToken();
    assertThat(token.getValue()).isEqualTo("bar");
    assertThat(token.getOriginalValue()).isEqualTo("bar");
    assertThat(token.getLine()).isEqualTo(1);
    assertThat(token.getColumn()).isEqualTo(4);

    assertThat(token.getTrivia()).hasSize(1);
    Trivia trivia = token.getTrivia().get(0);
    Token triviaToken = trivia.getToken();
    assertThat(triviaToken.getValue()).isEqualTo("foo ");
    assertThat(triviaToken.getOriginalValue()).isEqualTo("foo ");
    assertThat(triviaToken.getLine()).isEqualTo(1);
    assertThat(triviaToken.getColumn()).isEqualTo(0);
  }

  @Test
  public void should_skip_nodes() {
    char[] input = "foo".toCharArray();

    GrammarElementMatcher ruleMatcher1 = mockRuleMatcher("rule1");
    when(ruleMatcher1.hasToBeSkippedFromAst(Mockito.any(AstNode.class))).thenReturn(true);
    GrammarElementMatcher ruleMatcher2 = mockRuleMatcher("rule2");
    ParseNode node = new ParseNode(0, 3, Collections.EMPTY_LIST, ruleMatcher1);
    ParseNode parseTreeRoot = new ParseNode(0, 3, ImmutableList.of(node), ruleMatcher2);

    InputBuffer inputBuffer = new ImmutableInputBuffer(input);
    ParsingResult parsingResult = new ParsingResult(inputBuffer, true, parseTreeRoot, null);

    AstNode astNode = AstCreator.create(uri, parsingResult);
    System.out.println(AstXmlPrinter.print(astNode));

    assertThat(astNode.getType()).isSameAs(ruleMatcher2);
    assertThat(astNode.getName()).isEqualTo("rule2");
    assertThat(astNode.getFromIndex()).isEqualTo(0);
    assertThat(astNode.getToIndex()).isEqualTo(3);
    assertThat(astNode.hasChildren()).isFalse();
    assertThat(astNode.getToken()).isNull();
  }

  private static GrammarElementMatcher mockRuleMatcher(String name) {
    return when(mock(GrammarElementMatcher.class).getName()).thenReturn(name).getMock();
  }

  private static TokenMatcher mockTokenMatcher(TokenType tokenType) {
    return when(mock(TokenMatcher.class).getTokenType()).thenReturn(tokenType).getMock();
  }

}
