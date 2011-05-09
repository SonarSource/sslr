/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class MatcherAdapter extends Matcher {
	private Matcher matcher;
	
	public MatcherAdapter(Matcher matcher) {
		this.matcher = matcher;
	}
	
	@Override
	public Matcher[] getChildren() {
		return this.matcher.getChildren();
	}

	@Override
	public void setChild(int i, Matcher child) {
		this.matcher.setChild(i, child);
	}

	@Override
	public void setParentRule(RuleImpl parentRule) {
		this.matcher.setParentRule(parentRule);
	}

	@Override
	public RuleImpl getRule() {
		return this.matcher.getRule();
	}

	@Override
	public int matchToIndex(ParsingState parsingState) {
		return this.matcher.matchToIndex(parsingState);
	}

	@Override
	public boolean hasToBeSkippedFromAst(AstNode node) {
		return this.matcher.hasToBeSkippedFromAst(node);
	}

	@Override
	public AstNode match(ParsingState parsingState) {
		return this.matcher.match(parsingState);
	}

	@Override
	public boolean equals(Object obj) {
		return this.matcher.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.matcher.hashCode();
	}

	@Override
	public String toString() {
		return this.matcher.toString();
	}

}
