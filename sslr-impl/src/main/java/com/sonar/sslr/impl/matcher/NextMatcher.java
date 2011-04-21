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

  public String toString() {
    return "(" + super.children[0] + ")next";
  }

}
