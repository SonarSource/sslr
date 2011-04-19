/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.ArrayList;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class AtLeastOneMatcher extends Matcher {

  private final Matcher[] matchers;

  public AtLeastOneMatcher(Matcher... matchers) {
    this.matchers = matchers;
  }

  public AstNode match(ParsingState parsingState) {
  	ArrayList<AstNode> childNodes = new ArrayList<AstNode>();
    int startIndex = parsingState.lexerIndex;

    for (int i = 0; i < matchers.length; i++) {
    	if (matchers[i].isMatching(parsingState) >= 0) {
    		childNodes.add(matchers[i].match(parsingState));
    	}
    }
    
    if (childNodes.size() < 1) {
    	/* At least one constraint violated */
    	throw RecognitionExceptionImpl.create();
    }

    AstNode astNode = new AstNode(this, "AtLeastOneMatcher", parsingState.peekTokenIfExists(startIndex, this));
    for (AstNode child: childNodes) {
      astNode.addChild(child);
    }
    return astNode;
  }

  public String toString() {
    StringBuilder expr = new StringBuilder("atLeastOne(");
    for (int i = 0; i < matchers.length; i++) {
      expr.append(matchers[i]);
      if (i < matchers.length - 1) {
        expr.append(", ");
      }
    }
    expr.append(")");
    return expr.toString();
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    for (Matcher matcher : matchers) {
      matcher.setParentRule(parentRule);
    }
  }
}
