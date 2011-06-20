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

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.AdaptersDecorator;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ProfilerTest {

  private PrintStream stream;
  private Profiler profiler = new Profiler();

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;

    public Rule getRootRule() {
      return root;
    }

  }
  
  public Parser<MyTestGrammar> createParser() {
    return Parser.builder((MyTestGrammar)new MyTestGrammarDecorator()).optSetLexer(IdentifierLexer.create()).optEnableProfiler().build();
  }

  private class MyTestGrammarDecorator extends MyTestGrammar {

    public MyTestGrammarDecorator() {
      root.is(and(or(and(rule1, "fail"), rule1), EOF));
      rule1.is("hehe");

    }
  }

  @Test
  public void ok() {
  	Parser<MyTestGrammar> p = createParser();
    //p.disableMemoizer();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.stream = new PrintStream(baos);
    p.parse("hehe");
    
    System.out.println();
    System.out.println("root: " + MatcherTreePrinter.printWithAdapters(((RuleDefinition)p.getGrammar().root).getRule()));
    System.out.println("original root: " + MatcherTreePrinter.printWithAdapters(((RuleMatcherAdapter)((RuleDefinition)p.getGrammar().root).getRule()).getRuleImpl()));
    System.out.println("rule1: " + MatcherTreePrinter.printWithAdapters(((RuleDefinition)p.getGrammar().rule1).getRule()));
  }

}
