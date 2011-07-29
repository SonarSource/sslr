/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.StatelessMatcher;

public class DelayMatcher extends StatelessMatcher {
	
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
	public AstNode matchWorker(ParsingState parsingState) {
		long startTime = getCpuTime();
		while (getCpuTime() < startTime + delay*1000000L);
		
		return super.children[0].match(parsingState);
	}
	
	private static long getCpuTime() {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime( ) : 0L;
	}

}
