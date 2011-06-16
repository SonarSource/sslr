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

public class Profiler implements ParsingEventListener {

	public void enterRule(RuleMatcher rule, ParsingState parsingState) {
		// TODO Auto-generated method stub
		
	}

	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState,
			AstNode astNode) {
		// TODO Auto-generated method stub
		
	}

	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState,
			BacktrackingException re) {
		// TODO Auto-generated method stub
		
	}

	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		// TODO Auto-generated method stub
		
	}

	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState,
			AstNode astNode) {
		// TODO Auto-generated method stub
		
	}

	public void exitWithoutMatchMatcher(Matcher matcher,
			ParsingState parsingState, BacktrackingException re) {
		// TODO Auto-generated method stub
		
	}

}
