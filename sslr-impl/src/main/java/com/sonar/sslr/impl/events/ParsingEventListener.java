/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.RuleImpl;

public interface ParsingEventListener {
	/* Rule level */
	void enterRule(RuleImpl rule, ParsingState parsingState);
	
	void exitWithMatchRule(RuleImpl rule, ParsingState parsingState);
	
	void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re);
	
	/* TODO: Matcher level */
}
