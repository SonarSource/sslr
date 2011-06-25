/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingEvent;

public class AdjacentMatcher extends MemoizedMatcher {
	
  protected AdjacentMatcher(Matcher matcher) {
  	super(matcher);
  }

  public AstNode matchWorker(ParsingState parsingState) {
    int index = parsingState.lexerIndex;
    Token nextToken = parsingState.peekToken(index, this);
    Token previousToken = parsingState.readToken(index - 1);
    if (nextToken.getColumn() <= previousToken.getColumn() + previousToken.getValue().length()
        && nextToken.getLine() == previousToken.getLine()) {
      AstNode node = new AstNode(null, "adjacentMatcher", nextToken);
      node.addChild(super.children[0].match(parsingState));
      return node;
    } else {
      throw BacktrackingEvent.create();
    }
  }
  
  @Override
  public String toString() {
  	return "adjacent";
  }
  
}
