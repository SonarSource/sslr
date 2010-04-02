/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.api.TokenType;

public class ExclusiveTillMatcher extends Matcher {

  private Matcher[] matchers;

  public ExclusiveTillMatcher(Matcher... matchers) {
    this.matchers = matchers;
  }

  public AstNode match(ParsingState parsingState) {
    Token nextToken = parsingState.peekTokenIfExists(parsingState.lexerIndex, this);
    int nextTokenLine = 0;
    int nextTokenColumn = 0;
    if (nextToken != null) {
      nextTokenLine = nextToken.getLine();
      nextTokenColumn = nextToken.getColumn();
    }
    AstNode astNode = new AstNode(this, "exclusiveTillMatcher", nextToken);
    StringBuilder builder = new StringBuilder();
    while (nothingMatch(parsingState)) {
      builder.append(parsingState.popToken(this).getValue());
      builder.append(" ");
    }
    astNode.addChild(new AstNode(new Token(new WordsTokenType(), builder.toString(), nextTokenLine, nextTokenColumn)));
    return astNode;
  }

  private boolean nothingMatch(ParsingState parsingState) {
    for (Matcher matcher : matchers) {
      if (matcher.isMatching(parsingState)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    for (Matcher matcher : matchers) {
      matcher.setParentRule(parentRule);
    }
  }

  public String toString() {
    StringBuilder expr = new StringBuilder("(");
    for (int i = 0; i < matchers.length; i++) {
      expr.append(matchers[i]);
      if (i < matchers.length - 1) {
        expr.append(" | ");
      }
    }
    expr.append(")exclusiveTill");
    return expr.toString();
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
