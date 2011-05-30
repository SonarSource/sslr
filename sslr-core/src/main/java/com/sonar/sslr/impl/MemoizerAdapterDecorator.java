/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.util.HashSet;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.impl.matcher.*;

class MemoizerAdapterDecorator<GRAMMAR extends Grammar> implements GrammarDecorator<GRAMMAR> {
	private HashSet<Matcher> visited;
	
	private void decorateMatcher(Matcher matcher) {
		/* Visitor logic */
		if (visited.contains(matcher)) return; /* This matcher was already visited */
		visited.add(matcher);
		
		/* Memoizer adapter logic */
		Matcher children[] = matcher.getChildren();
		for (int i = 0; i < children.length; i++) {
			Matcher child = children[i];
			
			if (child instanceof MemoizerMatcher) {
				/* The child is a MemoizerMatcher, so it does not have to be memoized again */
				
				/* However, the grand children might still need to be memoized */
				for (Matcher grandChild: child.getChildren()) {
					decorateMatcher(grandChild);
				}
			}
			else {
				/* The child should be memoized, if the strategy allows it */
				
				/* Memoizing strategy: Memoize everything but TokenValueMatcher, TokenTypeMatcher and TokenTypeClassMatcher */
				if (!(child instanceof TokenValueMatcher) && !(child instanceof TokenTypeMatcher) && !(child instanceof TokenTypeClassMatcher)) {
					children[i] = new MemoizerMatcher(child);
				}

				decorateMatcher(child);
			}
		}
	}
	
	public void decorate(GRAMMAR grammar) {
		RuleMatcher root = ((RuleBuilder)grammar.getRootRule()).getRule();
		
		/* The root rule does not have to be memoized */

		visited = new HashSet<Matcher>();
		decorateMatcher(root); /* Change the whole tree, recursively! */
	}
	
}
