/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.GrammarRuleLifeCycleManager;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.events.IdentifierLexer;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

import static com.sonar.sslr.impl.matcher.Matchers.*;
import static org.junit.Assert.*;

public class EventAdapterDecoratorTest {
	
	public class MyTestGrammar implements Grammar {
		
		public Rule root;
		public Rule rule1;
		public Rule rule2;
		
		public Rule getRootRule() {
	    return root;
	  }
		
	}
	
	private class MyTestGrammarParser extends Parser<MyTestGrammar> {
		
	  public MyTestGrammarParser(boolean cyclic, MyTestGrammar g) {
	  	super(g, new IdentifierLexer(), (cyclic) ? new MyTestGrammarDecoratorCyclic() : new MyTestGrammarDecorator());
	  }
	  
	}
	
	private class MyTestGrammarDecorator implements GrammarDecorator<MyTestGrammar> {
		public void decorate(MyTestGrammar t) {
			GrammarRuleLifeCycleManager.initializeRuleFields(t, MyTestGrammar.class);
			
			t.root.is("bonjour", longestOne(t.rule1, t.rule2), and("olaa", "uhu"), EOF);
			t.rule1.is("hehe");
			t.rule2.is("hehe", "huhu");
		}
	}
	
	private class MyTestGrammarDecoratorCyclic implements GrammarDecorator<MyTestGrammar> {
		public void decorate(MyTestGrammar t) {
			GrammarRuleLifeCycleManager.initializeRuleFields(t, MyTestGrammar.class);

			t.root.is(or(and("four", "PLUS", t.root), "four")); /* A recursive grammar */
		}
	}
	
	@Test
	public void ok() {
		MyTestGrammarParser p = new MyTestGrammarParser(false, new MyTestGrammar());
		p.disableMemoizer();
		p.enableExtendedStackTrace();
		p.parse("bonjour hehe huhu olaa uhu");
		assertEquals(p.getRootRule().getDefinition(), "RuleImplAdapter(root.is(MatcherAdapter(and(MatcherAdapter(\"bonjour\"), MatcherAdapter(longestOne(RuleImplAdapter(rule1), RuleImplAdapter(rule2))), MatcherAdapter(and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\"))), MatcherAdapter(EOF)))))");
		
		p = new MyTestGrammarParser(true, new MyTestGrammar());
		p.disableMemoizer();
		p.enableExtendedStackTrace();
		p.parse("four PLUS four PLUS four");
		assertEquals(p.getRootRule().getDefinition(), "RuleImplAdapter(root.is(MatcherAdapter(or(MatcherAdapter(and(MatcherAdapter(\"four\"), MatcherAdapter(\"PLUS\"), RuleImplAdapter(root))), MatcherAdapter(\"four\")))))");
		
		p = new MyTestGrammarParser(false, new MyTestGrammar());
		p.enableExtendedStackTrace();
		p.parse("bonjour hehe huhu olaa uhu");
		assertEquals(p.getRootRule().getDefinition(), "RuleImplAdapter(root.is(MatcherAdapter(MemoizerMatcher(MatcherAdapter(and(MatcherAdapter(\"bonjour\"), MatcherAdapter(MemoizerMatcher(MatcherAdapter(longestOne(MatcherAdapter(MemoizerMatcher(RuleImplAdapter(rule1))), MatcherAdapter(MemoizerMatcher(RuleImplAdapter(rule2))))))), MatcherAdapter(MemoizerMatcher(MatcherAdapter(and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\"))))), MatcherAdapter(EOF)))))))");
	}
	
}