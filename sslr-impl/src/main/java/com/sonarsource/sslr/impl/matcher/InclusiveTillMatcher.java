/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.api.TokenType;
import com.sonarsource.sslr.impl.ParsingState;

public class InclusiveTillMatcher extends Matcher {

  private Matcher matcher;

  public InclusiveTillMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    AstNode astNode = new AstNode(this, "tillMatcher", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));
    StringBuilder builder = new StringBuilder();
    while (!matcher.isMatching(parsingState)) {
      builder.append(parsingState.popToken(this).getValue());
      builder.append(" ");
    }
    astNode.addChild(new AstNode(new Token(new WordsTokenType(), builder.toString())));
    astNode.addChild(matcher.match(parsingState));
    return astNode;
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    matcher.setParentRule(parentRule);
  }

  public String toString() {
    return "(" + matcher + ")till";
  }

  static class WordsTokenType implements TokenType {

    public String getName() {
      return "WORDS";
    }

    public boolean hasToBeSkippedFromAst() {
      return false;
    }

    public String getValue() {
      return "WORDS";
    }

  }
}
