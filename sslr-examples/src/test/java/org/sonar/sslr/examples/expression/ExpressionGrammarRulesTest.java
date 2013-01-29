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
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class ExpressionGrammarRulesTest {

  private LexerlessGrammarBuilder b = ExpressionGrammarRules.createGrammarBuilder();

  @Test
  public void test() {
    assertThat(b.build().rule(ExpressionGrammarRules.ROOT))
        .matches("20 * ( 2 + 2 ) - var")
        .matches("1 + 1")
        .notMatches("1 +");
  }

  @Test
  public void should_override() {
    b.rule(ExpressionGrammarRules.PLUS).override("plus ");
    assertThat(b.build().rule(ExpressionGrammarRules.ROOT))
        .matches("1 plus 1")
        .notMatches("1 + 1");
  }

}
