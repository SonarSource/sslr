/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.lexer.Token;
import com.sonarsource.lexer.TokenType;
import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.ast.AstNode;

public class ExclusiveTillMatcher extends Matcher {

  private Matcher[] matchers;

  public ExclusiveTillMatcher(Matcher... matchers) {
    this.matchers = matchers;
  }

  public AstNode match(ParsingState parsingState) {
    AstNode astNode = new AstNode(this, "exclusiveTillMatcher", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));
    StringBuilder builder = new StringBuilder();
    while (nothingMatch(parsingState)) {
      builder.append(parsingState.popToken(this).getValue());
      builder.append(" ");
    }
    astNode.addChild(new AstNode(new Token(new WordsTokenType(), builder.toString())));
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
  public void setParentRule(Rule parentRule) {
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
