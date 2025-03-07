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
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.AstNode;
import org.junit.Test;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.LexerlessGrammar;
import org.sonar.sslr.parser.ParserAdapter;

import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.assertThat;
import static org.sonar.sslr.tests.Assertions.assertThat;

public class ExpressionGrammarTest {

  private final LexerlessGrammarBuilder b = ExpressionGrammar.createGrammarBuilder();

  /**
   * This test demonstrates how to use {@link org.sonar.sslr.tests.Assertions} to test rules of grammar.
   */
  @Test
  public void rules() {
    assertThat(b.build().rule(ExpressionGrammar.EXPRESSION))
      .matches("1 + 1")
      .notMatches("1 +")
      .matches("20 * ( 2 + 2 ) - var");
  }

  /**
   * This test demonstrates how to use {@link ParserAdapter} to parse and construct AST.
   */
  @Test
  public void ast() {
    ParserAdapter<LexerlessGrammar> parser = new ParserAdapter<>(StandardCharsets.UTF_8, b.build());
    AstNode rootNode = parser.parse("2 + var");
    assertThat(rootNode.getType()).isSameAs(ExpressionGrammar.EXPRESSION);

    AstNode astNode = rootNode;
    assertThat(astNode.getNumberOfChildren()).isEqualTo(1);
    assertThat(astNode.getChildren().get(0).getType()).isSameAs(ExpressionGrammar.ADDITIVE_EXPRESSION);

    astNode = rootNode.getChildren().get(0);
    assertThat(astNode.getNumberOfChildren()).isEqualTo(3);
    assertThat(astNode.getChildren().get(0).getType()).isSameAs(ExpressionGrammar.NUMBER);
    assertThat(astNode.getChildren().get(1).getType()).isSameAs(ExpressionGrammar.PLUS);
    assertThat(astNode.getChildren().get(2).getType()).isSameAs(ExpressionGrammar.VARIABLE);
  }

}
