/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class AndMatcher extends Matcher {
	
  public AndMatcher(Matcher... matchers) {
  	super(matchers);
  }

  public AstNode match(ParsingState parsingState) {
    AstNode[] childNodes = new AstNode[super.children.length];
    int startIndex = parsingState.lexerIndex;

    for (int i = 0; i < super.children.length; i++) {
      childNodes[i] = super.children[i].match(parsingState);
    }

    AstNode astNode = new AstNode(this, "AllMatcher", parsingState.peekTokenIfExists(startIndex, this));
    for (int i = 0; i < childNodes.length; i++) {
      astNode.addChild(childNodes[i]);
    }
    return astNode;
  }
  
  @Override
  public String getDefinition(boolean isRoot) {
    StringBuilder expr = new StringBuilder("and(");
    for (int i = 0; i < super.children.length; i++) {
      expr.append(super.children[i].getDefinition(false));
      if (i < super.children.length - 1) {
        expr.append(", ");
      }
    }
    expr.append(")");
    return expr.toString();
  }
  
}
