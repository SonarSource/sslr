/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.HashMap;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class Profiler implements ParsingEventListener {
	
	private HashMap<RuleMatcher, Integer> rulesCounter = new HashMap<RuleMatcher, Integer>();

	public void enterRule(RuleMatcher rule, ParsingState parsingState) {
		Integer hits = rulesCounter.get(rule);
		if (hits == null) hits = 0;
		
		rulesCounter.put(rule, hits + 1);
	}

	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
		
	}

	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, BacktrackingException re) {

	}

	public void enterMatcher(Matcher matcher, ParsingState parsingState) {

	}

	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {

	}

	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, BacktrackingException re) {

	}

}
