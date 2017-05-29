/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.junit.Before;
import org.junit.Test;
import org.sonar.sslr.parser.ParseError;
import org.sonar.sslr.parser.ParseErrorFormatter;
import org.sonar.sslr.parser.ParseRunner;
import org.sonar.sslr.parser.ParsingResult;

import static org.fest.assertions.Assertions.assertThat;

public class ExpressionGrammarTest {

  private ExpressionGrammar grammar;

  @Before
  public void setUp() {
    grammar = new ExpressionGrammar();
  }

  @Test
  public void match() {
    String inputString = "20 * ( 2 + 2 ) - var";
    char[] input = inputString.toCharArray();
    ParseRunner parseRunner = new ParseRunner(grammar.root);
    ParsingResult result = parseRunner.parse(input);
    assertThat(result.isMatched()).isTrue();
    ParseTreePrinter.print(result.getParseTreeRoot(), input);
    assertThat(ParseTreePrinter.leafsToString(result.getParseTreeRoot(), input)).as("full-fidelity").isEqualTo(inputString);
  }

  @Test
  public void mismatch() {
    String inputString = "term +";
    char[] input = inputString.toCharArray();
    ParseRunner parseRunner = new ParseRunner(grammar.root);
    ParsingResult result = parseRunner.parse(input);
    assertThat(result.isMatched()).isFalse();
    ParseError parseError = result.getParseError();
    System.out.print(new ParseErrorFormatter().format(parseError));
    assertThat(parseError.getErrorIndex()).isEqualTo(6);
  }

  @Test
  public void prefix_match() {
    String inputString = "term +";
    char[] input = inputString.toCharArray();
    ParseRunner parseRunner = new ParseRunner(grammar.expression);
    ParsingResult result = parseRunner.parse(input);
    assertThat(result.isMatched()).isTrue();
  }

  @Test
  public void should_mock() {
    String inputString = "term plus term";
    char[] input = inputString.toCharArray();
    grammar.term.mock();
    grammar.plus.mock();
    ParseRunner parseRunner = new ParseRunner(grammar.root);
    ParsingResult result = parseRunner.parse(input);
    assertThat(result.isMatched()).isTrue();
    ParseTreePrinter.print(result.getParseTreeRoot(), input);
    assertThat(ParseTreePrinter.leafsToString(result.getParseTreeRoot(), input)).as("full-fidelity").isEqualTo(inputString);
  }

  @Test
  public void should_create_ast() throws Exception {
    String inputString = "20 * 2 + 2 - var";
    ExpressionGrammar grammar = new ExpressionGrammar();
    char[] input = inputString.toCharArray();
    ParseRunner parseRunner = new ParseRunner(grammar.root);
    ParsingResult result = parseRunner.parse(input);

    AstNode astNode = AstCreator.create(result, new LocatedText(null, inputString.toCharArray()));
    System.out.println(astNode.getTokens());
    System.out.println(AstXmlPrinter.print(astNode));

    assertThat(astNode.getTokens()).hasSize(7);

    Token firstToken = astNode.getToken();
    assertThat(firstToken.getLine()).isEqualTo(1);
    assertThat(firstToken.getColumn()).isEqualTo(0);
    assertThat(firstToken.getValue()).isEqualTo("20");
    assertThat(firstToken.getOriginalValue()).isEqualTo("20");

    Token tokenWithTrivia = astNode.getFirstDescendant(grammar.mul).getToken();
    assertThat(tokenWithTrivia.getLine()).isEqualTo(1);
    assertThat(tokenWithTrivia.getColumn()).isEqualTo(3);
    assertThat(tokenWithTrivia.getTrivia()).hasSize(1);
    assertThat(tokenWithTrivia.getValue()).isEqualTo("*");
    assertThat(tokenWithTrivia.getOriginalValue()).isEqualTo("*");
  }

}
