/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.GrammarFunctions;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleMatcher;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static org.fest.assertions.Assertions.assertThat;

public class ParsingEventListenerTest {

  private PrintStream stream;

  private final ParsingEventListener parsingEventListener = new ParsingEventListener() {

    @Override
    public void enterRule(RuleMatcher rule, ParsingState parsingState) {
      stream.println("Entered rule " + rule.getName() + " at index " + parsingState.lexerIndex);
    }

    @Override
    public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
      stream.println("Exit rule " + rule.getName() + " with match until index " + parsingState.lexerIndex);
    }

    @Override
    public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
      stream.println("Exit rule " + rule.getName() + " without match");
    }

    @Override
    public void enterMatcher(Matcher matcher, ParsingState parsingState) {
      stream.println("Entered matcher " + MatcherTreePrinter.print(matcher) + " at index " + parsingState.lexerIndex);
    }

    @Override
    public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
      stream.println("Exit matcher " + MatcherTreePrinter.print(matcher) + " with match until index " + parsingState.lexerIndex);
    }

    @Override
    public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
      stream.println("Exit matcher " + MatcherTreePrinter.print(matcher) + " without match");
    }

  };

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;
    public Rule rule2;

    @Override
    public Rule getRootRule() {
      return root;
    }

  }

  public Parser<MyTestGrammar> createParser() {
    return Parser.builder((MyTestGrammar) new MyTestGrammarDecorator()).withLexer(IdentifierLexer.create())
        .setParsingEventListeners(parsingEventListener).build();
  }

  private class MyTestGrammarDecorator extends MyTestGrammar {

    public MyTestGrammarDecorator() {
      root.is("bonjour", longestOne(rule1, rule2), and("olaa", "uhu"), EOF);
      rule1.is("hehe");
      rule2.is(GrammarFunctions.Advanced.memoizeMatches(and("hehe", "huhu")));
    }
  }

  @Test
  public void ok() {
    Parser<MyTestGrammar> p = createParser();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.stream = new PrintStream(baos);
    p.parse("bonjour hehe huhu olaa uhu");

    ByteArrayOutputStream baosExpected = new ByteArrayOutputStream();
    PrintStream expected = new PrintStream(baosExpected);
    expected.println("Entered rule root at index 0");
    expected.println("Entered matcher and(\"bonjour\", longestOne(rule1, rule2), and(\"olaa\", \"uhu\"), EOF) at index 0");
    expected.println("Entered matcher \"bonjour\" at index 0");
    expected.println("Exit matcher \"bonjour\" with match until index 1");
    expected.println("Entered matcher longestOne(rule1, rule2) at index 1");
    expected.println("Entered rule rule1 at index 1");
    expected.println("Entered matcher \"hehe\" at index 1");
    expected.println("Exit matcher \"hehe\" with match until index 2");
    expected.println("Exit rule rule1 with match until index 2");
    expected.println("Entered rule rule2 at index 1");
    expected.println("Entered matcher and(\"hehe\", \"huhu\") at index 1");
    expected.println("Entered matcher \"hehe\" at index 1");
    expected.println("Exit matcher \"hehe\" with match until index 2");
    expected.println("Entered matcher \"huhu\" at index 2");
    expected.println("Exit matcher \"huhu\" with match until index 3");
    expected.println("Exit matcher and(\"hehe\", \"huhu\") with match until index 3");
    expected.println("Exit rule rule2 with match until index 3");
    expected.println("Entered rule rule2 at index 1");

    expected.println("Entered matcher and(\"hehe\", \"huhu\") at index 1");
    expected.println("Exit matcher and(\"hehe\", \"huhu\") with match until index 3");

    expected.println("Exit rule rule2 with match until index 3");
    expected.println("Exit matcher longestOne(rule1, rule2) with match until index 3");
    expected.println("Entered matcher and(\"olaa\", \"uhu\") at index 3");
    expected.println("Entered matcher \"olaa\" at index 3");
    expected.println("Exit matcher \"olaa\" with match until index 4");
    expected.println("Entered matcher \"uhu\" at index 4");
    expected.println("Exit matcher \"uhu\" with match until index 5");
    expected.println("Exit matcher and(\"olaa\", \"uhu\") with match until index 5");
    expected.println("Entered matcher EOF at index 5");
    expected.println("Exit matcher EOF with match until index 6");
    expected.println("Exit matcher and(\"bonjour\", longestOne(rule1, rule2), and(\"olaa\", \"uhu\"), EOF) with match until index 6");
    expected.println("Exit rule root with match until index 6");

    assertThat(baos.toString()).isEqualTo(baosExpected.toString());
  }

}
