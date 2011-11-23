/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

public class AstNodeUtils {

  public static AstNode createAstNode(String name) {
    return new AstNode(GenericTokenType.IDENTIFIER, name, new Token(GenericTokenType.LITERAL, "dummy"));
  }

}
