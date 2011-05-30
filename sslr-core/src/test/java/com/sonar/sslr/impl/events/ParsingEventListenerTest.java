/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.api.GrammarFunctions.Standard.and;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ParsingEventListenerTest {

  private PrintStream stream;

  private ParsingEventListener parsingEventListener = new ParsingEventListener() {

    public void enterRule(RuleMatcher rule, ParsingState parsingState) {
      stream.println("Entered rule " + rule.getName() + " at index " + parsingState.lexerIndex);
    }

    public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
      stream.println("Exit rule " + rule.getName() + " with match until index " + parsingState.lexerIndex);
    }

    public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, RecognitionExceptionImpl re) {
      stream.println("Exit rule " + rule.getName() + " without match");
    }

    public void enterMatcher(Matcher matcher, ParsingState parsingState) {
      stream.println("Entered matcher " + MatcherTreePrinter.printWithAdapters(matcher) + " at index " + parsingState.lexerIndex);
    }

    public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
      stream
          .println("Exit matcher " + MatcherTreePrinter.printWithAdapters(matcher) + " with match until index " + parsingState.lexerIndex);
    }

    public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState, RecognitionExceptionImpl re) {
      stream.println("Exit matcher " + MatcherTreePrinter.printWithAdapters(matcher) + " without match");
    }

  };

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;
    public Rule rule2;

    public Rule getRootRule() {
      return root;
    }

  }

  private class MyTestGrammarParser extends Parser<MyTestGrammar> {

    public MyTestGrammarParser(MyTestGrammar g) {
      super(g, IdentifierLexer.create(), new MyTestGrammarDecorator(), new EventAdapterDecorator<MyTestGrammar>(parsingEventListener));
    }

  }

  private class MyTestGrammarDecorator implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is("bonjour", longestOne(t.rule1, t.rule2), and("olaa", "uhu"), EOF);
      t.rule1.is("hehe");
      t.rule2.is("hehe", "huhu");
    }
  }

  @Test
  public void ok() {
    MyTestGrammarParser p = new MyTestGrammarParser(new MyTestGrammar());
    p.disableMemoizer();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.stream = new PrintStream(baos);
    p.parse("bonjour hehe huhu olaa uhu");

    StringBuilder expected = new StringBuilder();
    expected.append("Entered rule root at index 0" + System.getProperty("line.separator"));
    expected
        .append("Entered matcher and(MatcherAdapter(\"bonjour\"), MatcherAdapter(longestOne(RuleImplAdapter(rule1), RuleImplAdapter(rule2))), MatcherAdapter(and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\"))), MatcherAdapter(EOF)) at index 0"
            + System.getProperty("line.separator"));
    expected.append("Entered matcher \"bonjour\" at index 0" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"bonjour\" with match until index 1" + System.getProperty("line.separator"));
    expected.append("Entered matcher longestOne(RuleImplAdapter(rule1), RuleImplAdapter(rule2)) at index 1"
        + System.getProperty("line.separator"));
    expected.append("Entered rule rule1 at index 1" + System.getProperty("line.separator"));
    expected.append("Entered matcher \"hehe\" at index 1" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"hehe\" with match until index 2" + System.getProperty("line.separator"));
    expected.append("Exit rule rule1 with match until index 2" + System.getProperty("line.separator"));
    expected.append("Entered rule rule2 at index 1" + System.getProperty("line.separator"));
    expected.append("Entered matcher and(MatcherAdapter(\"hehe\"), MatcherAdapter(\"huhu\")) at index 1"
        + System.getProperty("line.separator"));
    expected.append("Entered matcher \"hehe\" at index 1" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"hehe\" with match until index 2" + System.getProperty("line.separator"));
    expected.append("Entered matcher \"huhu\" at index 2" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"huhu\" with match until index 3" + System.getProperty("line.separator"));
    expected.append("Exit matcher and(MatcherAdapter(\"hehe\"), MatcherAdapter(\"huhu\")) with match until index 3"
        + System.getProperty("line.separator"));
    expected.append("Exit rule rule2 with match until index 3" + System.getProperty("line.separator"));
    expected.append("Entered rule rule2 at index 1" + System.getProperty("line.separator"));
    expected.append("Entered matcher and(MatcherAdapter(\"hehe\"), MatcherAdapter(\"huhu\")) at index 1"
        + System.getProperty("line.separator"));
    expected.append("Entered matcher \"hehe\" at index 1" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"hehe\" with match until index 2" + System.getProperty("line.separator"));
    expected.append("Entered matcher \"huhu\" at index 2" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"huhu\" with match until index 3" + System.getProperty("line.separator"));
    expected.append("Exit matcher and(MatcherAdapter(\"hehe\"), MatcherAdapter(\"huhu\")) with match until index 3"
        + System.getProperty("line.separator"));
    expected.append("Exit rule rule2 with match until index 3" + System.getProperty("line.separator"));
    expected.append("Exit matcher longestOne(RuleImplAdapter(rule1), RuleImplAdapter(rule2)) with match until index 3"
        + System.getProperty("line.separator"));
    expected.append("Entered matcher and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\")) at index 3"
        + System.getProperty("line.separator"));
    expected.append("Entered matcher \"olaa\" at index 3" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"olaa\" with match until index 4" + System.getProperty("line.separator"));
    expected.append("Entered matcher \"uhu\" at index 4" + System.getProperty("line.separator"));
    expected.append("Exit matcher \"uhu\" with match until index 5" + System.getProperty("line.separator"));
    expected.append("Exit matcher and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\")) with match until index 5"
        + System.getProperty("line.separator"));
    expected.append("Entered matcher EOF at index 5" + System.getProperty("line.separator"));
    expected.append("Exit matcher EOF with match until index 6" + System.getProperty("line.separator"));
    expected
        .append("Exit matcher and(MatcherAdapter(\"bonjour\"), MatcherAdapter(longestOne(RuleImplAdapter(rule1), RuleImplAdapter(rule2))), MatcherAdapter(and(MatcherAdapter(\"olaa\"), MatcherAdapter(\"uhu\"))), MatcherAdapter(EOF)) with match until index 6"
            + System.getProperty("line.separator"));
    expected.append("Exit rule root with match until index 6" + System.getProperty("line.separator"));

    assertEquals(baos.toString(), expected.toString());
  }

}
