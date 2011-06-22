/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public abstract class ParsingEventListener {
	/* Parsing level */
	public void beginLex() { };
	
	public void endLex() { };
	
	public void beginParse() { };
	
	public void endParse() { };
	
	/* Rule level */
	public void enterRule(RuleMatcher rule, ParsingState parsingState) { };
	
	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) { };
	
	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, BacktrackingException re) { };
	
	/* Matcher level */
	public void enterMatcher(Matcher matcher, ParsingState parsingState) { };
	
	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) { };
	
	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, BacktrackingException re) { };
	
	/* Memoization level */
	public void memoizerHit(Matcher matcher, ParsingState parsingState) { };
	
	public void memoizerMiss(Matcher matcher, ParsingState parsingState) { };
}
