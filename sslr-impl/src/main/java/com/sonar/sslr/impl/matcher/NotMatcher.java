/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class NotMatcher extends Matcher {
	
  public NotMatcher(Matcher matcher) {
    super(matcher);
  }

  public AstNode match(ParsingState parsingState) {
    if (super.children[0].isMatching(parsingState)) {
      throw RecognitionExceptionImpl.create();
    } else {
      return null;
    }
  }
  
  @Override
  public String getDefinition(boolean isRoot) {
  	return "not(" + super.children[0].getDefinition(false) + ")";
  }

}
