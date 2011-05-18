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
import com.sonar.sslr.impl.matcher.RuleImpl;

public class RuleImplAdapterTest {
	
	private RuleImpl rule;
	private String state = "";
	
	@Before
	public void init() {
		rule = new RuleImpl("rule");
		rule.is("bonjour");
	}
	
	@Test
	public void testGetDefinition() {
		RuleImplAdapter adapter = new RuleImplAdapter(null, rule);
		
		assertEquals(adapter.getDefinition(true, true), "RuleImplAdapter(rule.is(\"bonjour\"))");
		assertEquals(adapter.getDefinition(false, true), "RuleImplAdapter(rule)");
		assertEquals(adapter.getDefinition(true, false), "rule.is(\"bonjour\")");
		assertEquals(adapter.getDefinition(false, false), "rule");
	}
	
	@Test
	public void testMatch() {
		
		RuleImplAdapter adapter = new RuleImplAdapter(new ParsingEventListener() {
			
			public void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re) {
				state += "exitWithoutMatchRule ";
			}
			
			public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
				state += "exitWithoutMatchMatcher ";
			}
			
			public void exitWithMatchRule(RuleImpl rule, ParsingState parsingState, AstNode astNode) {
				state += "exitWithMatchRule ";
			}
			
			public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
				state += "exitWithMatchMatcher ";
			}
			
			public void enterRule(RuleImpl rule, ParsingState parsingState) {
				state += "enterRule ";
			}
			
			public void enterMatcher(Matcher matcher, ParsingState parsingState) {
				state += "enterMatcher ";
			}
		}, rule);
		
		state = "";
		assertThat(adapter, match("bonjour"));
		assertEquals(state, "enterRule exitWithMatchRule ");
		
		state = "";
		assertThat(adapter, not(match("test")));
		assertEquals(state, "enterRule exitWithoutMatchRule ");
	}
	
}
