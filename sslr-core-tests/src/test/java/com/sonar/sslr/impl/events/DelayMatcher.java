/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;

public class DelayMatcher extends Matcher {
	
	private final long delay;
	
	public static Matcher delay(long delay, Object... matchers) {
		if (matchers.length == 0) {
			throw new IllegalArgumentException("At least one matcher must be given.");
		}
		
		return new DelayMatcher(delay, and(matchers));
	}
	
	public DelayMatcher(long delay, Matcher matcher) {
		super(matcher);
		
		this.delay = delay;
	}

	@Override
	public AstNode match(ParsingState parsingState) {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + delay);
		
		return super.children[0].match(parsingState);
	}

}
