/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.ArrayList;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

public class AtLeastOneMatcher extends MemoizedMatcher {

  protected AtLeastOneMatcher(Matcher... matchers) {
    super(matchers);
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    ArrayList<AstNode> childNodes = new ArrayList<AstNode>();
    int startIndex = parsingState.lexerIndex;

    for (int i = 0; i < super.children.length; i++) {
      if (super.children[i].isMatching(parsingState)) {
        childNodes.add(super.children[i].match(parsingState));
      }
    }

    if (childNodes.size() < 1) {
      /* At least one constraint violated */
      throw BacktrackingEvent.create();
    }

    AstNode astNode = new AstNode(null, "AtLeastOneMatcher", parsingState.peekTokenIfExists(startIndex, this));
    for (AstNode child : childNodes) {
      astNode.addChild(child);
    }
    return astNode;
  }

  @Override
  public String toString() {
    return "atLeastOne";
  }

}
