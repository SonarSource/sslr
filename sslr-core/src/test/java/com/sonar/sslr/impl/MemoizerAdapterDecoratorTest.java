/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.api.GrammarFunctions.Standard.and;
import static com.sonar.sslr.api.GrammarFunctions.Standard.or;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.events.IdentifierLexer;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;

public class MemoizerAdapterDecoratorTest {

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;
    public Rule rule2;

    public LeftRecursiveRule left;

    public Rule getRootRule() {
      return root;
    }

  }

  private class MyTestGrammarParser extends Parser<MyTestGrammar> {

    public MyTestGrammarParser(boolean leftRecursive, boolean cyclic, MyTestGrammar g) {
      super(g, new IdentifierLexer(), (leftRecursive) ? new MyTestGrammarDecoratorLeft() : ((cyclic) ? new MyTestGrammarDecoratorCyclic()
          : new MyTestGrammarDecorator()));
    }

  }

  private class MyTestGrammarDecorator implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is("bonjour", longestOne(t.rule1, t.rule2), and("olaa", "uhu"), EOF);
      t.rule1.is("hehe");
      t.rule2.is("hehe", "huhu");
    }
  }

  private class MyTestGrammarDecoratorCyclic implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is(or(and("four", "PLUS", t.root), "four")); /* A recursive grammar */
    }
  }

  private class MyTestGrammarDecoratorLeft implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is(t.left, EOF); /* A left recursive grammar */
      t.left.is(or(and(t.left, "PLUS", t.left), "three"));
    }
  }

  @Test
  public void ok() {
    MyTestGrammarParser p = new MyTestGrammarParser(false, false, new MyTestGrammar());
    p.parse("bonjour hehe huhu olaa uhu");
    assertEquals(
        MatcherTreePrinter.printWithAdapters(p.getRootRule().getRule()),
        "root.is(MemoizerMatcher(and(\"bonjour\", MemoizerMatcher(longestOne(MemoizerMatcher(rule1), MemoizerMatcher(rule2))), MemoizerMatcher(and(\"olaa\", \"uhu\")), EOF)))");

    p = new MyTestGrammarParser(false, true, new MyTestGrammar());
    p.parse("four PLUS four PLUS four");
    assertEquals(MatcherTreePrinter.printWithAdapters(p.getRootRule().getRule()),
        "root.is(MemoizerMatcher(or(MemoizerMatcher(and(\"four\", \"PLUS\", MemoizerMatcher(root))), \"four\")))");

    p = new MyTestGrammarParser(true, false, new MyTestGrammar());
    p.parse("three PLUS three PLUS three");
    assertEquals(MatcherTreePrinter.printWithAdapters(p.getRootRule().getRule()),
        "root.is(MemoizerMatcher(and(MemoizerMatcher(left), EOF)))");
  }

}
