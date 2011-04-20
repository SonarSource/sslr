/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.LeftRecursiveRuleImpl;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class RuleEventAdapter extends LeftRecursiveRuleImpl {
	private RuleImpl rule;
	private ParsingEventListener listener;

	public RuleEventAdapter(RuleImpl rule, ParsingEventListener listener) {
		super(rule.toString());
		this.rule = rule;
		this.listener = listener;
	}

	public RuleImpl is(Object... matchers) {
		rule.is(matchers);
		return this;
	}

	public RuleImpl override(Object... matchers) {
		rule.override(matchers);
		return this;
	}

	public RuleImpl or(Object... matchers) {
		rule.or(matchers);
		return this;
	}

	public RuleImpl and(Object... matchers) {
		rule.and(matchers);
		return this;
	}

	public RuleImpl orBefore(Object... matchers) {
		rule.orBefore(matchers);
		return this;
	}

	public RuleImpl isOr(Object... matchers) {
		rule.isOr(matchers);
		return this;
	}

	public Rule setListener(AstListener listener) {
		rule.setListener(listener);
		return this;
	}

	public RuleImpl skip() {
		rule.skip();
		return this;
	}

	public Rule skipIf(AstNodeType policy) {
		rule.skipIf(policy);
		return this;
	}

	public Rule skipIfOneChild() {
		rule.skipIfOneChild();
		return this;
	}

	public void mockUpperCase() {
		rule.mockUpperCase();
	}

	public void mock() {
		rule.mock();
	}

	public void recoveryRule() {
		rule.recoveryRule();
	}

	public Rule plug(Class adapterClass) {
		rule.plug(adapterClass);
		return this;
	}

	@Override
	public void setParentRule(RuleImpl parentRule) {
		rule.setParentRule(parentRule);
	}

	@Override
	public AstNode match(ParsingState parsingState) {
		listener.enterRule(rule, parsingState);

		try {
			AstNode result = rule.match(parsingState);
			listener.exitWithMatchRule(rule, parsingState);
			return result;
		} catch (RecognitionExceptionImpl re) {
			listener.exitWithoutMatchRule(rule, parsingState);
			throw re;
		}
	}

	public void endParsing() {
		if (rule instanceof LeftRecursiveRule) {
			((LeftRecursiveRule) rule).endParsing();
		}
	}
}
