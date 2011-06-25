/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.events.ParsingEventListener;

public abstract class NonMemoizedMatcher extends Matcher {
	
	public NonMemoizedMatcher(Matcher... children) {
		super(children);
	}

  public final AstNode match(ParsingState parsingState) {
  	enterEvent(parsingState);
  	
  	try {
  		AstNode astNode = matchWorker(parsingState);  		
  		exitWithMatchEvent(parsingState, astNode);
    	return astNode;
  	} catch (BacktrackingException re) {
  		exitWithoutMatchEvent(parsingState, re);
  		throw re;
  	}
  	
  }

  protected abstract AstNode matchWorker(ParsingState parsingState);
  
}
