/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;

public class NextMatcher extends Matcher {

	protected NextMatcher(Matcher matcher) {
    super(matcher);
  }

  public AstNode matchWorker(ParsingState parsingState) {
    if (super.children[0].isMatching(parsingState)) {
      return null;
    }
    throw BacktrackingException.create();
  }
  
  @Override
  public String toString() {
  	return "next";
  }

}
