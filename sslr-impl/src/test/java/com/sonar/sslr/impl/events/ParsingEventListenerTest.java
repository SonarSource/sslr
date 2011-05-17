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

public class ParsingEventListenerTest {
	private PrintStream stream;

	private ParsingEventListener parsingEventListener = new ParsingEventListener(){

		public void enterRule(RuleImpl rule, ParsingState parsingState) {
			stream.println("Entered rule " + rule.getName() + " at index " + parsingState.lexerIndex);
		}

		public void exitWithMatchRule(RuleImpl rule, ParsingState parsingState, AstNode astNode) {
			stream.println("Exit rule " + rule.getName() + " with match until index " + parsingState.lexerIndex);
		}

		public void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re) {
			stream.println("Exit rule " + rule.getName() + " without match");
		}

		public void enterMatcher(Matcher matcher, ParsingState parsingState) {
			stream.println("Entered matcher " + matcher + " at index " + parsingState.lexerIndex);
		}

		public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
			stream.println("Exit matcher " + matcher + " with match until index " + parsingState.lexerIndex);
		}

		public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
			stream.println("Exit matcher " + matcher + " without match");
		}
		
	};
	
	public class MyTestGrammar implements Grammar {
		
		public Rule root;
		public Rule rule1;
		public Rule rule2;
		
		public Rule getRootRule() {
	    return root;
	  }
		
	}
	
	private class MyTestGrammarParser extends Parser<MyTestGrammar> {
		
	  public MyTestGrammarParser(MyTestGrammar g) {
	  	super(g, new IdentifierLexer(), new MyTestGrammarDecorator(), new EventAdapterDecorator<MyTestGrammar>(parsingEventListener));
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
	
	@Test
	public void ok() {
		MyTestGrammarParser p = new MyTestGrammarParser(new MyTestGrammar());
		p.disableMemoizer();
		
		/*
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(baos);
		p.parse("bonjour hehe huhu olaa uhu");
		System.out.println(baos.toString());
		*/
	}
	
}
