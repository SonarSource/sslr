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
		System.out.println("enter rule! " + rule.getName());
	}

	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
		
	}

	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, BacktrackingException re) {

	}

	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		System.out.println("Enter matcher " + matcher);
	}

	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {

	}

	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, BacktrackingException re) {

	}

}
