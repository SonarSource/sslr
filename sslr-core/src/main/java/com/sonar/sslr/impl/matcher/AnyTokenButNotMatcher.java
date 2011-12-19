/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

public final class AnyTokenButNotMatcher extends StatelessMatcher {

  protected AnyTokenButNotMatcher(Matcher matcher) {
    super(matcher);
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    if (super.children[0].isMatching(parsingState)) {
      throw BacktrackingEvent.create();
    } else {
      return new AstNode(parsingState.popToken(this));
    }
  }

  @Override
  public String toString() {
    return "anyTokenButNot";
  }

}
