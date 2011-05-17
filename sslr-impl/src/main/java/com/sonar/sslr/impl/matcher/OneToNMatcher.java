/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class OneToNMatcher extends Matcher {

  public OneToNMatcher(Matcher matcher) {
    super(matcher);
  }

  public AstNode match(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    AstNode astNode = null;
    boolean match = true;
    int loop = 0;
    do {
      match = super.children[0].isMatching(parsingState);
      if (match) {
        if (astNode == null) {
          astNode = new AstNode(this, "oneToNMatcher", parsingState.peekTokenIfExists(startIndex, this));
        }
        astNode.addChild(super.children[0].match(parsingState));
        loop++;
      }
    } while (match);
    if (loop == 0) {
      throw RecognitionExceptionImpl.create();
    }
    return astNode;
  }
  
  @Override
  public String getDefinition(boolean isRoot, boolean isVerbose) {
  	return "one2n(" + super.children[0].getDefinition(false, isVerbose) + ")";
  }

}
