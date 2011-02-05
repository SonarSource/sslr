/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

public class DslTokenType implements TokenType {

  private String name;
  private TokenFormatter formatter;

  public DslTokenType(String name) {
    this.name = name;
  }

  public DslTokenType(String name, TokenFormatter formatter) {
    this.name = name;
    this.formatter = formatter;
  }

  public final String getName() {
    return name;
  }

  public final String getValue() {
    return name;
  }

  public Object formatDslValue(String value) {
    if (formatter != null) {
      return formatter.format(value);
    }
    return null;
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return false;
  }

  public static final DslTokenType WORD = new DslTokenType("WORD");
  public static final DslTokenType LITERAL = new DslTokenType("LITERAL", new LiteralTokenFormatter());
  public static final DslTokenType INTEGER = new DslTokenType("INTEGER", new IntegerTokenFormatter());
  public static final DslTokenType PUNCTUATOR = new DslTokenType("PUNCTUATOR");
  public static final DslTokenType DOUBLE = new DslTokenType("DOUBLE", new DoubleTokenFormatter());

  private static class IntegerTokenFormatter implements TokenFormatter {

    public Object format(String value) {
      return Integer.parseInt(value);
    }
  }

  private static class DoubleTokenFormatter implements TokenFormatter {

    public Object format(String value) {
      return Double.parseDouble(value);
    }
  }

  private static class LiteralTokenFormatter implements TokenFormatter {

    public Object format(String value) {
      return value.substring(1, value.length() - 1);
    }
  }
}
