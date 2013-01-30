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

import com.google.common.base.Charsets;
import com.sonar.sslr.api.AstNode;
import org.junit.Test;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.LexerlessGrammar;
import org.sonar.sslr.parser.ParserAdapter;

import static org.fest.assertions.Assertions.assertThat;
import static org.sonar.sslr.tests.Assertions.assertThat;

public class ExpressionGrammarRulesTest {

  private LexerlessGrammarBuilder b = ExpressionGrammarRules.createGrammarBuilder();

  @Test
  public void test() {
    assertThat(b.build().rule(ExpressionGrammarRules.EXPRESSION))
        .matches("20 * ( 2 + 2 ) - var")
        .matches("1 + 1")
        .notMatches("1 +");
  }

  @Test
  public void should_override() {
    b.rule(ExpressionGrammarRules.PLUS).override("plus ");
    assertThat(b.build().rule(ExpressionGrammarRules.EXPRESSION))
        .matches("1 plus 1")
        .notMatches("1 + 1");
  }

  @Test
  public void ast() {
    ParserAdapter<LexerlessGrammar> parser = new ParserAdapter<LexerlessGrammar>(Charsets.UTF_8, b.build());
    AstNode rootNode = parser.parse("2 + var");
    assertThat(rootNode.getType()).isSameAs(ExpressionGrammarRules.EXPRESSION);

    AstNode astNode = rootNode;
    assertThat(astNode.getNumberOfChildren()).isEqualTo(1);
    assertThat(astNode.getChild(0).getType()).isSameAs(ExpressionGrammarRules.ADDITIVE_EXPRESSION);

    astNode = rootNode.getChild(0);
    assertThat(astNode.getNumberOfChildren()).isEqualTo(3);
    assertThat(astNode.getChild(0).getType()).isSameAs(ExpressionGrammarRules.NUMBER);
    assertThat(astNode.getChild(1).getType()).isSameAs(ExpressionGrammarRules.PLUS);
    assertThat(astNode.getChild(2).getType()).isSameAs(ExpressionGrammarRules.VARIABLE);
  }

}
