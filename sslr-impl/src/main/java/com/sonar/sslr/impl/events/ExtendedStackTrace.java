/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ParsingState;

public class ExtendedStackTrace implements ParsingEventListener {

	public void enterRule(Rule rule, ParsingState parsingState) {
		System.out.println("enterRule " + rule);
	}

	public void exitWithMatchRule(Rule rule, ParsingState parsingState) {
		System.out.println("exitWithMatchRule " + rule);
	}

	public void exitWithoutMatchRule(Rule rule, ParsingState parsingState) {
		System.out.println("exitWithoutMatchRule " + rule);
	}

}
