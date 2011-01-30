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
    return value;
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return false;
  }
}
