/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;
import com.sonarsource.parser.ast.AstNode;

public class EndOfFileMatcher extends Matcher {

  private Matcher matcher;

  public EndOfFileMatcher() {
  }

  public EndOfFileMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    AstNode astNode = new AstNode(this, "EnfOfFile", null);
    if (matcher != null) {
      matcher.match(parsingState);
    }
    if (parsingState.hasNextToken()) {
      throw RecognitionException.create();
    }
    return astNode;
  }

  @Override
  public void setParentRule(Rule parentRule) {
    this.parentRule = parentRule;
    if (matcher != null) {
      matcher.setParentRule(parentRule);
    }
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (matcher != null) {
      builder.append(matcher.toString());
      builder.append(" ");
    }
    builder.append("EOF");
    return builder.toString();
  }
}
