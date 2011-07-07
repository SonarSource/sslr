/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;

public class InclusiveTillMatcher extends MemoizedMatcher {

  protected InclusiveTillMatcher(Matcher matcher) {
    super(matcher);
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    AstNode astNode = new AstNode(null, "till", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));

    while (!super.children[0].isMatching(parsingState)) {
    	Token token = parsingState.popToken(this);
      astNode.addChild(new AstNode(token));
    }
    
    astNode.addChild(super.children[0].match(parsingState));
    return astNode;
  }

  @Override
  public String toString() {
    return "till";
  }

}
