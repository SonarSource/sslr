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
package org.sonar.sslr.examples.expression;

import org.junit.Test;
import org.sonar.sslr.grammar.Grammar;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.LexerlessGrammar;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class ExpressionGrammarTest {

  @Test
  public void test() {
    // TODO Godin: should be a way to get a rule by type
    LexerlessGrammar grammar = ExpressionGrammarRules.createGrammar();
    assertThat(grammar.getRootRule())
        .matches("20 * ( 2 + 2 ) - var")
        .notMatches("1 +");
  }

  @Test
  public void should_mock() {
    // TODO Godin: I'd like to have a fluent interface to replace next 3 statements by 1
    LexerlessGrammarBuilder b = ExpressionGrammarRules.createGrammarBuilder();
    b.rule(ExpressionGrammarRules.PLUS).override("plus ");
    Grammar grammar = b.build();
    assertThat(grammar.rule(ExpressionGrammarRules.ROOT))
        .matches("1 plus 1");
  }

}
