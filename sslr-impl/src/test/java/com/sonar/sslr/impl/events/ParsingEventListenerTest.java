/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.GrammarRuleLifeCycleManager;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ParsingState;

import static com.sonar.sslr.impl.matcher.Matchers.*;
import static org.junit.Assert.*;

public class ParsingEventListenerTest {
	private int state;
	private boolean failed;
	
	private ParsingEventListener parsingEventListener = new ParsingEventListener(){

		public void enterRule(Rule rule, ParsingState parsingState) {
			if (state == 0 && rule.toString().equals("mytest")) state++;
			else if (state == 1 && rule.toString().equals("item_list_1")) state++;
			else if (state == 3 && rule.toString().equals("item_list_2")) state++;
			else failed = true;
		}

		public void exitWithMatchRule(Rule rule, ParsingState parsingState) {
			if (state == 4 && rule.toString().equals("item_list_2")) state++;
			else if (state == 5 && rule.toString().equals("mytest")) state++;
			else failed = true;
		}

		public void exitWithoutMatchRule(Rule rule, ParsingState parsingState) {
			if (state == 2 && rule.toString().equals("item_list_1")) state++;
			else failed = true;
		}
	};
	
	public class MyTestGrammar implements Grammar {
		
		public Rule mytest;
		public Rule item_list_1;
		public Rule item_list_2;
		
		public Rule getRootRule() {
	    return mytest;
	  }
	}
	
	private class MyTestGrammarDecorator implements GrammarDecorator<MyTestGrammar> {
		public void decorate(MyTestGrammar t) {
			GrammarRuleLifeCycleManager.initializeRuleFields(t, MyTestGrammar.class, parsingEventListener);
			
			t.mytest.is(or(t.item_list_1, t.item_list_2));
			t.item_list_1.is("hehe");
			t.item_list_2.is("huhu");
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
	  p.parse("huhu");
	  
	  assertTrue(state == 6 && !failed);
	}
}
