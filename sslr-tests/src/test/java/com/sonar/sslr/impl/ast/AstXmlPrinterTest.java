/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.ast;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class AstXmlPrinterTest {

  @Test
  public void testPrintRuleAstNode() {
    AstNode root = new AstNode(RuleDefinition.newRuleBuilder("expr"), "expr", new Token(new WordTokenType(), "word", 34, 12, null));
    assertEquals("<expr tokenValue=\"word\" tokenLine=\"34\" tokenColumn=\"12\"/>", AstXmlPrinter.print(root));
  }

  @Test
  public void testPrintWordAstNode() {
    AstNode root = new AstNode(new Token(new WordTokenType(), "myword", 0, 0, null));
    assertEquals("<WORD tokenValue=\"myword\" tokenLine=\"0\" tokenColumn=\"0\"/>", AstXmlPrinter.print(root));
  }

  @Test
  public void testPrintFullAstNode() {
    AstNode astNode = new AstNode(RuleDefinition.newRuleBuilder("expr"), "expr", null);
    astNode.addChild(new AstNode(new Token(new WordTokenType(), "x", 1, 0, null)));
    astNode.addChild(new AstNode(new Token(new WordTokenType(), "=")));
    astNode.addChild(new AstNode(new Token(new WordTokenType(), "4")));

    StringBuilder expectedResult = new StringBuilder();
    expectedResult.append("<expr>\n");
    expectedResult.append("  <WORD tokenValue=\"x\" tokenLine=\"1\" tokenColumn=\"0\"/>\n");
    expectedResult.append("  <WORD tokenValue=\"=\" tokenLine=\"1\" tokenColumn=\"0\"/>\n");
    expectedResult.append("  <WORD tokenValue=\"4\" tokenLine=\"1\" tokenColumn=\"0\"/>\n");
    expectedResult.append("</expr>");
    assertEquals(expectedResult.toString(), AstXmlPrinter.print(astNode));
  }

  private static class WordTokenType implements TokenType {

    public String getName() {
      return "WORD";
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

    public String getValue() {
      return "WORDS";
    }

  }

}
