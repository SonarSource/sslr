/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;
import com.sonarsource.parser.ast.AstNode;

public class OneToNMatcher extends Matcher {

  private Matcher matcher;

  public OneToNMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    AstNode astNode = null;
    boolean match = true;
    int loop = 0;
    do {
      match = matcher.isMatching(parsingState);
      if (match) {
        if (astNode == null) {
          astNode = new AstNode(this, "oneToNMatcher", parsingState.peekTokenIfExists(startIndex, this));
        }
        astNode.addChild(matcher.match(parsingState));
        loop++;
      }
    } while (match);
    if (loop == 0) {
      throw RecognitionException.create();
    }
    return astNode;
  }

  @Override
  public void setParentRule(Rule parentRule) {
    this.parentRule = parentRule;
    matcher.setParentRule(parentRule);
  }

  public String toString() {
    return "(" + matcher + ")+";
  }
}
