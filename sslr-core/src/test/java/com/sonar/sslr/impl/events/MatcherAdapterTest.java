/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.events;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleMatcher;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;

public class MatcherAdapterTest {
	
	private Matcher matcher;
	private String state = "";
	
	@Before
	public void init() {
		matcher = new TokenValueMatcher("bonjour");
	}
	
	@Test
	public void testGetDefinition() {
		MatcherAdapter adapter = new MatcherAdapter(null, matcher);
		
		assertEquals(MatcherTreePrinter.printWithAdapters(adapter), "MatcherAdapter(\"bonjour\")");
		assertEquals(MatcherTreePrinter.print(adapter), "\"bonjour\"");
	}
	
	@Test
	public void testMatch() {
		
		MatcherAdapter adapter = new MatcherAdapter(new ParsingEventListener() {
			
			public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, RecognitionExceptionImpl re) {
				state += "exitWithoutMatchRule ";
			}
			
			public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
				state += "exitWithoutMatchMatcher ";
			}
			
			public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
				state += "exitWithMatchRule ";
			}
			
			public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
				state += "exitWithMatchMatcher ";
			}
			
			public void enterRule(RuleMatcher rule, ParsingState parsingState) {
				state += "enterRule ";
			}
			
			public void enterMatcher(Matcher matcher, ParsingState parsingState) {
				state += "enterMatcher ";
			}
		}, matcher);
		
		state = "";
		assertThat(adapter, match("bonjour"));
		assertEquals(state, "enterMatcher exitWithMatchMatcher ");
		
		state = "";
		assertThat(adapter, not(match("test")));
		assertEquals(state, "enterMatcher exitWithoutMatchMatcher ");
	}
	
}
