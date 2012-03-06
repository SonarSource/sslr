/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.xpath;

import com.sonar.sslr.api.AstNode;

public class AstNodeDocument {

  private final AstNode wrappedAstNode;

  public AstNodeDocument(AstNode wrappedAstNode) {
    this.wrappedAstNode = wrappedAstNode;
  }

  public AstNode getWrappedAstNode() {
    return wrappedAstNode;
  }

}
