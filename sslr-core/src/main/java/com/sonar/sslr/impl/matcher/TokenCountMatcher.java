/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;

public class TokenCountMatcher extends MemoizedMatcher {
	
	private final Operator operator;
	private final int n;
	
	public enum Operator {
		EQUAL,
		LESS_THAN,
		GREATER_THAN
	}
	
  protected TokenCountMatcher(Operator operator, int n, Matcher matcher) {
  	super(matcher);
  	
  	this.operator = operator;
  	this.n = n;
  }

  public AstNode matchWorker(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    AstNode astNode = super.children[0].match(parsingState);
    int stopIndex = parsingState.lexerIndex;
    
    int consumedTokens = stopIndex - startIndex;
    
    switch (operator) {
    	case EQUAL:
    		if (consumedTokens != n) {
    			throw BacktrackingException.create();
    		}
    		break;
    	case LESS_THAN:
    		if (consumedTokens >= n) {
    			throw BacktrackingException.create();
    		}
    		break;
    	case GREATER_THAN:
    		if (consumedTokens <= n) {
    			throw BacktrackingException.create();
    		}
    		break;
    	default:
    		throw BacktrackingException.create();
    }
    
    return astNode;
  }
  
  @Override
  public String toString() {
  	return "tokenCount(TokenCountMatcher.Operator." + operator + ", " + n + ")";
  }
  
}
