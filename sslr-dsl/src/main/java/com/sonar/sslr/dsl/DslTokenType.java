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

  public DslTokenType(String name) {
    this.name = name;
  }

  public final String getName() {
    return name;
  }

  public final String getValue() {
    return name;
  }
  
  public String toString(){
    return name;
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return false;
  }

  public static final DslTokenType WORD = new DslTokenType("WORD");
  public static final DslTokenType LITERAL = new DslTokenType("LITERAL");
  public static final DslTokenType INTEGER = new DslTokenType("INTEGER");
  public static final DslTokenType PUNCTUATOR = new DslTokenType("PUNCTUATOR");
  public static final DslTokenType DOUBLE = new DslTokenType("DOUBLE");
}
