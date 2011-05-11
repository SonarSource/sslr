/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class ZeroToNMatcher extends Matcher {

  public ZeroToNMatcher(Matcher matcher) {
    super(matcher);
  }

  public AstNode match(ParsingState parsingState) {
    if (parsingState.hasNextToken()) {
      int startIndex = parsingState.lexerIndex;
      AstNode astNode = null;
      boolean match = true;
      do {
        match = super.children[0].isMatching(parsingState);
        if (match) {
          if (astNode == null) {
            astNode = new AstNode(this, "zeroToNMatcher", parsingState.peekTokenIfExists(startIndex, this));
          }
          astNode.addChild(super.children[0].match(parsingState));
        }
      } while (match);
      return astNode;
    } else {
      return null;
    }
  }
  
  @Override
  public String getDefinition(boolean isRoot) {
  	return "o2n(" + super.children[0].getDefinition(false) + ")";
  }
  
}
