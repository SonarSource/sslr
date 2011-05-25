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

public class TillNewLineMatcher extends Matcher {

  public TillNewLineMatcher() {
  	super();
  }

  public AstNode match(ParsingState parsingState) {
  	int currentLine = (parsingState.lexerIndex - 1 > 0) ? parsingState.readToken(parsingState.lexerIndex - 1).getLine() : 1;
  	
    for (int i = parsingState.lexerIndex; i < parsingState.lexerSize && parsingState.peekToken(this).getLine() == currentLine; i++) {
    	parsingState.popToken(this);
    }
  	
    return null;
  }
  
  @Override
  public String toString() {
  	return "tillNewLine()";
  }
  
}
