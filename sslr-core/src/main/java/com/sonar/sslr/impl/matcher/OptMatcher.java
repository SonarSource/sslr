/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class OptMatcher extends Matcher {

  protected OptMatcher(Matcher matcher) {
    super(matcher);
  }

  public AstNode matchWorker(ParsingState parsingState) {
    if (super.children[0].isMatching(parsingState)) {
      return super.children[0].match(parsingState);
    }
    return null;
  }
  
  @Override
  public String toString() {
  	return "opt";
  }

}
