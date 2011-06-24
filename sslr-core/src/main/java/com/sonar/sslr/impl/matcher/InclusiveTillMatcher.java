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

public class InclusiveTillMatcher extends Matcher {

	protected InclusiveTillMatcher(Matcher matcher) {
  	super(matcher);
  }

  public AstNode matchWorker(ParsingState parsingState) {
    AstNode astNode = new AstNode(null, "tillMatcher", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));
    StringBuilder builder = new StringBuilder();
    while (!super.children[0].isMatching(parsingState)) {
      builder.append(parsingState.popToken(this).getValue());
      builder.append(" ");
    }
    astNode.addChild(new AstNode(new Token(new WordsTokenType(), builder.toString())));
    astNode.addChild(super.children[0].match(parsingState));
    return astNode;
  }
  
  @Override
  public String toString() {
  	return "till";
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
