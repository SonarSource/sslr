/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.ast;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;

public class SkipFromAstIfOnlyOneChild implements AstNodeSkippingPolicy {

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return node.getNumberOfChildren() == 1;
  }

  public static AstNodeSkippingPolicy oneChild() {
    return new SkipFromAstIfOnlyOneChild();
  }

}
