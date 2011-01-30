/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

public class DefaultDslTokenType {

  public static final DslTokenType WORD = new DslTokenType("WORD");
  public static final DslTokenType LITERAL = new DslTokenType("LITERAL", new LiteralTokenFormatter());
  public static final DslTokenType INTEGER = new DslTokenType("INTEGER", new IntegerTokenFormatter());
  public static final DslTokenType PUNCTUATOR = new DslTokenType("PUNCTUATOR");
  public static final DslTokenType FLOAT = new DslTokenType("FLOAT", new DoubleTokenFormatter());
  public static final DslTokenType EOF = new DslTokenType("EOF");
  public static final DslTokenType EOL = new DslTokenType("EOL");

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
