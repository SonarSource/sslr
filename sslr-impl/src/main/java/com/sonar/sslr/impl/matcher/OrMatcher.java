/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class OrMatcher extends Matcher {

  public OrMatcher(Matcher... matchers) {
    super(matchers);
  }

  public AstNode match(ParsingState parsingState) {
    for (Matcher matcher : super.children) {
      if (matcher.isMatching(parsingState)) {
        return matcher.match(parsingState);
      }
    }
    throw RecognitionExceptionImpl.create();
  }
  
  @Override
  public String getDefinition(boolean isRoot, boolean isVerbose) {
    StringBuilder expr = new StringBuilder("or(");
    for (int i = 0; i < super.children.length; i++) {
      expr.append(super.children[i].getDefinition(false, isVerbose));
      if (i < super.children.length - 1) {
        expr.append(", ");
      }
    }
    expr.append(")");
    return expr.toString();
  }

}
