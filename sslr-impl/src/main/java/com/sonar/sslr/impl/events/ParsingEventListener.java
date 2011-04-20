/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ParsingState;

public interface ParsingEventListener {
	void enterRule(Rule rule, ParsingState parsingState);
	
	void exitWithMatchRule(Rule rule, ParsingState parsingState);
	
	void exitWithoutMatchRule(Rule rule, ParsingState parsingState);
}
