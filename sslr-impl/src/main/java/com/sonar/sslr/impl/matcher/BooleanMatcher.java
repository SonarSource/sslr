/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class BooleanMatcher extends Matcher {

  private final boolean internalState;

  public BooleanMatcher(boolean internalState) {
  	super();
  	
    this.internalState = internalState;
  }

  public AstNode match(ParsingState parsingState) {
    parsingState.peekToken(this);
    if (internalState) {
      return new AstNode(this, "trueMatcher", parsingState.popToken(this));
    } else {
      throw RecognitionExceptionImpl.create();
    }
  }
  
  @Override
  public String getDefinition(boolean isRoot) {
  	return (internalState) ? "isTrue()" : "isFalse()";
  }

}
