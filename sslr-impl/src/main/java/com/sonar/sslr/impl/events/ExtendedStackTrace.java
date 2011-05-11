/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.Stack;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class ExtendedStackTrace implements ParsingEventListener {
	private Stack<Matcher> currentStack = new Stack<Matcher>();
	private Stack<RuleImpl> longestStack = new Stack<RuleImpl>();
	private int longestIndex = -1;
	
	public void enterRule(RuleImpl rule, ParsingState parsingState) {
		currentStack.push(rule);
	}

	public void exitWithMatchRule(RuleImpl rule, ParsingState parsingState, AstNode astNode) {
		currentStack.pop();
	}

	public void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re) {
		currentStack.pop();
	}

	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		currentStack.push(matcher);
	}

	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
		if (parsingState.lexerIndex > longestIndex) {
			/* New longest path! */
			longestIndex = parsingState.lexerIndex;
			
			longestStack = new Stack<RuleImpl>();
			for (Matcher currentMatcher: currentStack) {
				if (currentMatcher instanceof RuleImpl) longestStack.push((RuleImpl)currentMatcher);
			}
		}
		currentStack.pop();
	}

	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
		currentStack.pop();
	}
	
	public void printStackTrace() {
		System.out.println("Stack trace:");
		System.out.println("------------");
		
		if (longestStack.size() == 0) {
			System.out.println("Not a single match.");
		}
		else {
			RuleImpl rule = longestStack.pop();
			System.out.println(rule.getName());
			
			while (longestStack.size() > 0) {
				System.out.println(" at " + longestStack.pop().getName());
			}
		}
		
		System.out.println("End of stack trace");
	}
	
}
