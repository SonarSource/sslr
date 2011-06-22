/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ParsingState;

public class ExclusiveTillMatcher extends Matcher {
	
	protected ExclusiveTillMatcher(Matcher... matchers) {
  	super(matchers);
  }

  public AstNode match(ParsingState parsingState) {
    Token nextToken = parsingState.peekTokenIfExists(parsingState.lexerIndex, this);
    int nextTokenLine = 0;
    int nextTokenColumn = 0;
    if (nextToken != null) {
      nextTokenLine = nextToken.getLine();
      nextTokenColumn = nextToken.getColumn();
    }
    AstNode astNode = new AstNode(null, "exclusiveTillMatcher", nextToken);
    StringBuilder builder = new StringBuilder();
    while (nothingMatch(parsingState)) {
      builder.append(parsingState.popToken(this).getValue());
      builder.append(" ");
    }
    astNode.addChild(new AstNode(new Token(new WordsTokenType(), builder.toString(), nextTokenLine, nextTokenColumn)));
    return astNode;
  }

  private boolean nothingMatch(ParsingState parsingState) {
    for (Matcher matcher : super.children) {
      if (matcher.isMatching(parsingState)) {
        return false;
      }
    }
    return true;
  }
  
  @Override
  public String toString() {
  	return "exclusiveTill";
  }

  static class WordsTokenType implements TokenType {

    public String getName() {
      return "WORDS";
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

    public String getValue() {
      return "WORDS";
    }

  }
  
}
