/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.IdentifierLexer;
import com.sonar.sslr.impl.events.RuleMatcherAdapter;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class AdaptersDecoratorTest {

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;
    public Rule rule2;
    
    public LeftRecursiveRule left;

    public Rule getRootRule() {
      return root;
    }

  }
  
  public Parser<MyTestGrammar> createParser(boolean leftRecursive, boolean cyclic) {
  	if (leftRecursive) {
  		return Parser.builder((MyTestGrammar)new MyTestGrammarLeft()).optSetLexer(IdentifierLexer.create()).build();
  	} else {
  		return Parser.builder((cyclic) ? new MyTestGrammarCyclic() : new MyTestGrammarAcyclic()).optSetLexer(IdentifierLexer.create()).build();
  	}
  }

  private class MyTestGrammarLeft extends MyTestGrammar {

    public MyTestGrammarLeft() {
      root.is(left, EOF); /* A left recursive grammar */
      left.is(or(and(left, "PLUS", left), "three"));
    }
  }
  
  private class MyTestGrammarAcyclic extends MyTestGrammar {

    public MyTestGrammarAcyclic() {
      root.is("bonjour", longestOne(rule1, rule2), and("olaa", "uhu"), EOF);
      rule1.is("hehe");
      rule2.is("hehe", "huhu");
    }
    
  }

  private class MyTestGrammarCyclic extends MyTestGrammar {

    public MyTestGrammarCyclic() {
      root.is(or(and("four", "PLUS", root), "four")); /* A recursive grammar */
    }
    
  }

  @Test
  public void okAdapters() {
  	Parser<MyTestGrammar> p = createParser(false, false);
    p.disableMemoizer();
    p.enableExtendedStackTrace();
    p.parse("bonjour hehe huhu olaa uhu");
    assertEquals(printNewRootRuleWithAdapters(p), "RuleImplAdapter(root)");
    assertEquals(
        printOriginalRootRuleWithAdapters(p),
        "root.is(MatcherAdapter(and(MatcherAdapter(\"bonjour\"), MatcherAdapter(longestOne(RuleImplAdapter(rule1), RuleImplAdapter(rule2))), MatcherAdapter(and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\"))), MatcherAdapter(EOF))))");

    p = createParser(false, true);
    p.disableMemoizer();
    p.enableExtendedStackTrace();
    p.parse("four PLUS four PLUS four");
    assertEquals(printNewRootRuleWithAdapters(p), "RuleImplAdapter(root)");
    assertEquals(
        printOriginalRootRuleWithAdapters(p),
        "root.is(MatcherAdapter(or(MatcherAdapter(and(MatcherAdapter(\"four\"), MatcherAdapter(\"PLUS\"), RuleImplAdapter(root))), MatcherAdapter(\"four\"))))");

    p = createParser(false, false);
    p.enableExtendedStackTrace();
    p.parse("bonjour hehe huhu olaa uhu");
    assertEquals(printNewRootRuleWithAdapters(p), "RuleImplAdapter(MemoizerMatcher(root))");
    assertEquals(
        printOriginalRootRuleWithAdapters(p),
        "root.is(MatcherAdapter(MemoizerMatcher(and(MatcherAdapter(\"bonjour\"), MatcherAdapter(MemoizerMatcher(longestOne(RuleImplAdapter(MemoizerMatcher(rule1)), RuleImplAdapter(MemoizerMatcher(rule2))))), MatcherAdapter(MemoizerMatcher(and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\")))), MatcherAdapter(EOF)))))");
  }
  
  @Test
  public void okMemoizer() {
  	Parser<MyTestGrammar> p = createParser(false, false);;
  	
    p.parse("bonjour hehe huhu olaa uhu");
    assertEquals(
    		MatcherTreePrinter.printWithAdapters(p.getRootRule().getRule()),
        "root.is(MemoizerMatcher(and(\"bonjour\", MemoizerMatcher(longestOne(MemoizerMatcher(rule1), MemoizerMatcher(rule2))), MemoizerMatcher(and(\"olaa\", \"uhu\")), EOF)))");

    p = createParser(false, true);
    p.parse("four PLUS four PLUS four");
    assertEquals(
    		MatcherTreePrinter.printWithAdapters(p.getRootRule().getRule()),
        "root.is(MemoizerMatcher(or(MemoizerMatcher(and(\"four\", \"PLUS\", MemoizerMatcher(root))), \"four\")))");

    p = createParser(true, false);
    p.parse("three PLUS three PLUS three");
    assertEquals(
    		MatcherTreePrinter.printWithAdapters(p.getRootRule().getRule()),
        "root.is(MemoizerMatcher(and(MemoizerMatcher(left), EOF)))");

  }
  
  private String printNewRootRuleWithAdapters(Parser<MyTestGrammar> p) {
    RuleMatcherAdapter ruleAdapter = (RuleMatcherAdapter) ((RuleDefinition) p.getGrammar().root).getRule();
    return MatcherTreePrinter.printWithAdapters(ruleAdapter);
  }

  private String printOriginalRootRuleWithAdapters(Parser<MyTestGrammar> p) {
    RuleMatcherAdapter ruleAdapter = (RuleMatcherAdapter) ((RuleDefinition) p.getGrammar().root).getRule();
    return MatcherTreePrinter.printWithAdapters(ruleAdapter.getRuleImpl());
  }
  
}
