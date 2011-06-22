/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.events.DelayMatcher.delay;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;

public class ProfilerTest {

  private PrintStream stream;

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;


    public Rule getRootRule() {
      return root;
    }

  }
  
  public Parser<MyTestGrammar> createParser(boolean memoization, MyTestGrammar myTestGrammarImpl) {
    return Parser.builder(myTestGrammarImpl).optSetLexer(IdentifierLexer.create()).withProfiler(true).withMemoizer(memoization).build();
  }

  private class MyTestGrammarDelay extends MyTestGrammar {

    public MyTestGrammarDelay() {
      root.is(delay(400, opt("huhu")), or(rule1, "nothing"), delay(600, "wtf"), EOF);
      rule1.is(delay(1500, "hehe"));
    }
    
  }
  
  private class MyTestGrammarBacktrack extends MyTestGrammar {

    public MyTestGrammarBacktrack() {
      root.is(or(rule1, and("hehe", "huhu", "hoho")), EOF);
      rule1.is("hehe", "huhu", delay(500, "hoho"), "BANG");
    }
    
  }

  @Test
  public void ok() {
  	Parser<MyTestGrammar> p = createParser(false, new MyTestGrammarDelay());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.stream = new PrintStream(baos);
    p.parse("hehe wtf");
    
    p.printProfiler(System.out);
    
    System.out.println("");
    
    p = createParser(false, new MyTestGrammarBacktrack());

    baos = new ByteArrayOutputStream();
    this.stream = new PrintStream(baos);
    p.parse("hehe huhu hoho");
    
    p.printProfiler(System.out);
  }

}
