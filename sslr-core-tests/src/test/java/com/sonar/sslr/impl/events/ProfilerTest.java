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
    public Rule rule2;

    public Rule getRootRule() {
      return root;
    }

  }
  
  public Parser<MyTestGrammar> createParser() {
    return Parser.builder((MyTestGrammar)new MyTestGrammarDecorator()).optSetLexer(IdentifierLexer.create()).optEnableProfiler().build();
  }

  private class MyTestGrammarDecorator extends MyTestGrammar {

    public MyTestGrammarDecorator() {
      root.is(and(or(and(rule1, "fail"), rule1, rule2), EOF));
      rule1.is("hehe");
      rule2.is("hoho");
    }
  }

  @Test
  public void ok() {
  	Parser<MyTestGrammar> p = createParser();
    //p.disableMemoizer();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.stream = new PrintStream(baos);
    p.parse("hoho");
    
    //p.printProfiler(System.out);
  }

}
