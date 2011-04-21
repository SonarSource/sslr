/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;

import org.junit.Ignore;
import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.GrammarRuleLifeCycleManager;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.RuleImpl;

import static com.sonar.sslr.impl.matcher.Matchers.*;
import static org.junit.Assert.*;

@Ignore
public class ParsingEventListenerTest {
	private int state;
	private boolean failed;
	
	private ParsingEventListener parsingEventListener = new ParsingEventListener(){

		public void enterRule(RuleImpl rule, ParsingState parsingState) {
			if (state == 0 && rule.toString().equals("mytest")) state++;
			else if (state == 1 && rule.toString().equals("item_list_1")) state++;
			else if (state == 3 && rule.toString().equals("item_list_2")) state++;
			else failed = true;
		}

		public void exitWithMatchRule(RuleImpl rule, ParsingState parsingState) {
			if (state == 4 && rule.toString().equals("item_list_2")) state++;
			else if (state == 5 && rule.toString().equals("mytest")) state++;
			else failed = true;
		}

		public void exitWithoutMatchRule(RuleImpl rule, ParsingState parsingState, RecognitionExceptionImpl re) {
			if (state == 2 && rule.toString().equals("item_list_1")) state++;
			else failed = true;
		}
	};
	
	private ExtendedStackTrace extendedStackTrace = new ExtendedStackTrace();
	
	public class MyTestGrammar implements Grammar {
		
		public Rule root;
		public Rule rule1;
		public Rule rule2;
		
		public Rule getRootRule() {
	    return root;
	  }
	}
	
	private class MyTestGrammarDecorator implements GrammarDecorator<MyTestGrammar> {
		public void decorate(MyTestGrammar t) {
			GrammarRuleLifeCycleManager.initializeRuleFields(t, MyTestGrammar.class, extendedStackTrace);
			
			t.root.is(longestOne(t.rule1, t.rule2), EOF);
			t.rule1.is("hehe");
			t.rule2.is("hehe", "huhu");
		}
	}
	
	private class MyTestGrammarParser extends Parser<MyTestGrammar> {
	  public MyTestGrammarParser(MyTestGrammar g) {
	  	super(g, g.getRootRule(), new IdentifierLexer());
	  	
	  	//this.setParsingEventListener(parsingEventListener);
	  	
	    setDecorators(new MyTestGrammarDecorator());
	  }
	}
	
	@Test
	public void ok() {
		state = 0;
		failed = false;
		MyTestGrammarParser p = new MyTestGrammarParser(new MyTestGrammar());
		
		try {
			p.parse("hehe huhu");
			System.out.println("*** PARSE SUCCESSFUL ***");
		}
		catch (Exception ex) {
			System.out.println("*** FAILED TO PARSE ***");
		}
		
		System.out.println("*** STACK TRACE ***");
		extendedStackTrace.printStackTrace();
		
	  assertTrue(state == 6 && !failed);
	}
	
}
