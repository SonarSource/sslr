/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Ignore;
import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.GrammarRuleLifeCycleManager;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

import static com.sonar.sslr.impl.matcher.Matchers.*;
import static org.junit.Assert.*;

public class ExtendedStackTraceTest {
	private PrintStream stream;
	
	public class MyTestGrammar implements Grammar {
		
		public Rule root;
		public Rule rule1;
		public Rule rule2;
		
		public Rule getRootRule() {
	    return root;
	  }
		
	}
	
	private class MyTestGrammarParser extends Parser<MyTestGrammar> {
		
	  public MyTestGrammarParser(boolean v1, MyTestGrammar g) {
	  	super(g, new IdentifierLexer(), (v1) ? new MyTestGrammarDecoratorV1() : new MyTestGrammarDecoratorV2());
	  }
	  
	}
	
	private class MyTestGrammarDecoratorV1 implements GrammarDecorator<MyTestGrammar> {
		public void decorate(MyTestGrammar t) {
			GrammarRuleLifeCycleManager.initializeRuleFields(t, MyTestGrammar.class);

			t.root.is("bonjour", longestOne(t.rule1, t.rule2), and("olaa", "uhu"), EOF);
			t.rule1.is("hehe");
			t.rule2.is("hehe", "huhu");
		}
	}
	
	private class MyTestGrammarDecoratorV2 implements GrammarDecorator<MyTestGrammar> {
		public void decorate(MyTestGrammar t) {
			GrammarRuleLifeCycleManager.initializeRuleFields(t, MyTestGrammar.class);

			t.root.is("bonjour", longestOne(t.rule1, t.rule2), and("olaa", "uhu"), EOF);
			t.rule1.is("hehe");
			t.rule2.is("hehe", "huhu", "wtf");
		}
	}
	
	@Test
	public void ok() {
		MyTestGrammarParser p = new MyTestGrammarParser(true, new MyTestGrammar());
		p.disableMemoizer();
		p.enableExtendedStackTrace();
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			p.parse("bonjour hehe huhu haha");
			throw new  IllegalStateException();
		} catch (RecognitionExceptionImpl ex) {
			p.printExtendedStackTrace(new PrintStream(baos));
		}
		
		StringBuilder expected = new StringBuilder();
		expected.append("Source Snippet:" + "\n");
		expected.append("---------------" + "\n");
		expected.append("  --> bonjour hehe huhu hahaEOF" + "\n");
		expected.append("---------------" + "\n");
		expected.append("" + "\n");
		expected.append("on matcher and(\"olaa\", \"uhu\")" + "\n");
		expected.append("       1 :     18  : \"olaa\" expected but \"haha\" [IDENTIFIER] found" + "\n");
		expected.append("at root" + "\n");
		expected.append("       1 :      0  : bonjour hehe huhu " + "\n");
		expected.append("" + "\n");
		expected.append("Last successful tokens:" + "\n");
		expected.append("-----------------------" + "\n");
		expected.append("  \"huhu\" at 1:13 consumed by root" + "\n");
		expected.append("  \"hehe\" at 1:8 consumed by root" + "\n");
		expected.append("  \"bonjour\" at 1:0 consumed by root" + "\n");
		
		assertEquals(baos.toString(), expected.toString());
		
		
		
		p = new MyTestGrammarParser(false, new MyTestGrammar());
		p.disableMemoizer();
		p.enableExtendedStackTrace();
	
		baos = new ByteArrayOutputStream();
		
		try {
			p.parse("bonjour hehe huhu haha");
			throw new  IllegalStateException();
		} catch (RecognitionExceptionImpl ex) {
			p.printExtendedStackTrace(new PrintStream(baos));
		}
		
		expected = new StringBuilder();
		expected.append("Source Snippet:" + "\n");
		expected.append("---------------" + "\n");
		expected.append("  --> bonjour hehe huhu hahaEOF" + "\n");
		expected.append("---------------" + "\n");
		expected.append("" + "\n");
		expected.append("on matcher \"wtf\"" + "\n");
		expected.append("       1 :     18  : \"wtf\" expected but \"haha\" [IDENTIFIER] found" + "\n");
		expected.append("at rule2" + "\n");
		expected.append("       1 :      8  : hehe huhu " + "\n");
		expected.append("at root" + "\n");
		expected.append("       1 :      0  : bonjour " + "\n");
		expected.append("" + "\n");
		expected.append("Last successful tokens:" + "\n");
		expected.append("-----------------------" + "\n");
		expected.append("  \"huhu\" at 1:13 consumed by rule2" + "\n");
		expected.append("  \"hehe\" at 1:8 consumed by rule2" + "\n");
		expected.append("  \"bonjour\" at 1:0 consumed by root" + "\n");
		
		assertEquals(baos.toString(), expected.toString());
		
	}
	
}
