/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
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
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ProfilerTest {

  private PrintStream stream;
  private Profiler profiler = new Profiler();

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
      super(g, IdentifierLexer.create(), new MyTestGrammarDecorator(), new EventAdapterDecorator<MyTestGrammar>(profiler));
    }

  }

  private class MyTestGrammarDecorator implements GrammarDecorator<MyTestGrammar> {

    public void decorate(MyTestGrammar t) {
      t.root.is(or(and(t.rule1, "fail"), t.rule1), EOF);
      t.rule1.is("hehe");
      t.rule2.is("hehe", "huhu");
    }
  }

  @Test
  public void ok() {
    MyTestGrammarParser p = new MyTestGrammarParser(new MyTestGrammar());
    //p.disableMemoizer();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.stream = new PrintStream(baos);
    p.parse("hehe");
  }

}
