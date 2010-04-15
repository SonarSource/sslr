/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.ast;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import com.sonar.sslr.impl.matcher.RuleImpl;

import static org.junit.Assert.assertEquals;

public class AstXmlPrinterTest {

  @Test
  public void testPrintRuleAstNode() {
    AstNode root = new AstNode(new RuleImpl("expr"), "expr", new Token(new WordTokenType(), "word", 34, 12, null));
    assertEquals("<expr value=\"word\" line=\"34\" col=\"12\"/>", AstXmlPrinter.print(root));
  }

  @Test
  public void testPrintWordAstNode() {
    AstNode root = new AstNode(new Token(new WordTokenType(), "myword", 0, 0, null));
    assertEquals("<WORD value=\"myword\" line=\"0\" col=\"0\"/>", AstXmlPrinter.print(root));
  }

  @Test
  public void testPrintFullAstNode() {
    AstNode astNode = new AstNode(new RuleImpl("expr"), "expr", null);
    astNode.addChild(new AstNode(new Token(new WordTokenType(), "x", 0, 0, null)));
    astNode.addChild(new AstNode(new Token(new WordTokenType(), "=")));
    astNode.addChild(new AstNode(new Token(new WordTokenType(), "4")));

    StringBuilder expectedResult = new StringBuilder();
    expectedResult.append("<expr>\n");
    expectedResult.append("  <WORD value=\"x\" line=\"0\" col=\"0\"/>\n");
    expectedResult.append("  <WORD value=\"=\" line=\"0\" col=\"0\"/>\n");
    expectedResult.append("  <WORD value=\"4\" line=\"0\" col=\"0\"/>\n");
    expectedResult.append("</expr>");
    assertEquals(expectedResult.toString(), AstXmlPrinter.print(astNode));
  }

  private static class WordTokenType implements TokenType {

    public String getName() {
      return "WORD";
    }

    public boolean hasToBeSkippedFromAst() {
      return false;
    }

    public String getValue() {
      return "WORDS";
    }

  }

}
