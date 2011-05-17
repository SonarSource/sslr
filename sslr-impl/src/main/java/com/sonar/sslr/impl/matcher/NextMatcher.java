/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class NextMatcher extends Matcher {

  public NextMatcher(Matcher matcher) {
    super(matcher);
  }

  public AstNode match(ParsingState parsingState) {
    if (super.children[0].isMatching(parsingState)) {
      return null;
    }
    throw RecognitionExceptionImpl.create();
  }
  
  @Override
  public String getDefinition(boolean isRoot, boolean isVerbose) {
  	return "next(" + super.children[0].getDefinition(false, isVerbose) + ")";
  }

}
