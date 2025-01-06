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
package com.sonar.sslr.impl.ast;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static com.sonar.sslr.test.lexer.MockHelper.mockTokenBuilder;
import static org.fest.assertions.Assertions.assertThat;

public class AstXmlPrinterTest {

  @Test
  public void testPrintRuleAstNode() {
    AstNode root = new AstNode(new RuleDefinition("expr"), "expr", mockTokenBuilder(new WordTokenType(), "word").setLine(34)
      .setColumn(12).build());

    assertThat(AstXmlPrinter.print(root)).isEqualTo("<expr tokenValue=\"word\" tokenLine=\"34\" tokenColumn=\"12\"/>");
  }

  @Test
  public void testPrintWordAstNode() {
    AstNode root = new AstNode(mockToken(new WordTokenType(), "myword"));
    assertThat(AstXmlPrinter.print(root)).isEqualTo("<WORD tokenValue=\"myword\" tokenLine=\"1\" tokenColumn=\"1\"/>");
  }

  @Test
  public void testPrintFullAstNode() {
    AstNode astNode = new AstNode(new RuleDefinition("expr"), "expr", null);
    astNode.addChild(new AstNode(mockToken(new WordTokenType(), "x")));
    astNode.addChild(new AstNode(mockToken(new WordTokenType(), "=")));
    astNode.addChild(new AstNode(mockToken(new WordTokenType(), "4")));
    astNode.addChild(new AstNode(mockToken(new WordTokenType(), "WORD")));

    String expectedResult = new StringBuilder()
      .append("<expr>\n")
      .append("  <WORD tokenValue=\"x\" tokenLine=\"1\" tokenColumn=\"1\"/>\n")
      .append("  <WORD tokenValue=\"=\" tokenLine=\"1\" tokenColumn=\"1\"/>\n")
      .append("  <WORD tokenValue=\"4\" tokenLine=\"1\" tokenColumn=\"1\"/>\n")
      .append("  <WORD tokenValue=\"WORD\" tokenLine=\"1\" tokenColumn=\"1\"/>\n")
      .append("</expr>")
      .toString();
    assertThat(AstXmlPrinter.print(astNode)).isEqualTo(expectedResult);
  }

  private static class WordTokenType implements TokenType {

    @Override
    public String getName() {
      return "WORD";
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

    @Override
    public String getValue() {
      return "WORDS";
    }

  }

}
