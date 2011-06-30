/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.events.DelayMatcher.delay;
import static com.sonar.sslr.impl.events.ProfilerStream.*;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*; 

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.mockito.internal.matchers.LessThan;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class ProfilerTest {
	
	Profiler profiler = new Profiler();

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;

    public Rule getRootRule() {
      return root;
    }

  }
  
  public Parser<MyTestGrammar> createParser(MyTestGrammar myTestGrammarImpl) {
    return Parser.builder(myTestGrammarImpl).withLexer(IdentifierLexer.create()).withParsingEventListeners(profiler).build();
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
  	Parser<MyTestGrammar> p = createParser(new MyTestGrammarDelay());
  	profiler.initialize();
    p.parse("hehe wtf");
    
    assertThat(nsToMs(profiler.getLexerCpuTime()), lessThan(50L));
    
    assertThat(nsToMs(profiler.getParserCpuTime()), greaterThan(2300L));
    assertThat(nsToMs(profiler.getParserCpuTime()), lessThan(2700L));
    
    assertThat(profiler.getHits(), equalTo(3L));
    
    assertThat(profiler.getBacktracks(), equalTo(0L));
    assertThat(profiler.getAverageBacktrackCpuTime(), equalTo(0.0));
    assertThat(profiler.getMaxBacktrackCpuTime(), equalTo(0L));
    assertThat(profiler.getAverageLookahead(), equalTo(0.0));
    assertThat(profiler.getMaxLookahead(), equalTo(0));
    
    assertThat(profiler.getMemoizedHits(), equalTo(1L));
    assertThat(profiler.getMemoizedMisses(), equalTo(2L));
    
    assertThat(nsToMs(profiler.getTotalNonMemoizedHitCpuTime()), greaterThan(2300L));
    assertThat(nsToMs(profiler.getTotalNonMemoizedHitCpuTime()), lessThan(2700L));
    
    for (Map.Entry<RuleMatcher, Profiler.RuleCounter> entry: profiler.ruleStats.entrySet()) {
    	Profiler.RuleCounter counter = entry.getValue();
    	
    	if (entry.getKey().getName().equals("rule1")) {
    		assertThat(counter.getHits(), equalTo(2));
    		
    		assertThat(counter.getBacktracks(), equalTo(0));
    		assertThat(counter.getAverageBacktracksCpuTime(), equalTo(0.0));
    		assertThat(counter.getMaxBacktrackCpuTime(), equalTo(0L));
    		assertThat(counter.getAverageLookahead(), equalTo(0.0));
    		assertThat(counter.getMaxLookahead(), equalTo(0));
    		
    		assertThat(counter.getMemoizedHits(), equalTo(1));
    		assertThat(counter.getMemoizedMisses(), equalTo(1));
    		
        assertThat(nsToMs(counter.getTotalNonMemoizedHitCpuTime()), greaterThan(1300L));
        assertThat(nsToMs(counter.getTotalNonMemoizedHitCpuTime()), lessThan(1700L));
    	} else if (entry.getKey().getName().equals("root")) {
    		assertThat(counter.getHits(), equalTo(1));
    		
    		assertThat(counter.getBacktracks(), equalTo(0));
    		assertThat(counter.getAverageBacktracksCpuTime(), equalTo(0.0));
    		assertThat(counter.getMaxBacktrackCpuTime(), equalTo(0L));
    		assertThat(counter.getAverageLookahead(), equalTo(0.0));
    		assertThat(counter.getMaxLookahead(), equalTo(0));
    		
    		assertThat(counter.getMemoizedHits(), equalTo(0));
    		assertThat(counter.getMemoizedMisses(), equalTo(1));
    		
        assertThat(nsToMs(counter.getTotalNonMemoizedHitCpuTime()), greaterThan(800L));
        assertThat(nsToMs(counter.getTotalNonMemoizedHitCpuTime()), lessThan(1200L));
    	} else {
    		throw new IllegalStateException();
    	}
    }
    
    p = createParser(new MyTestGrammarBacktrack());
    profiler.initialize();
    p.parse("hehe huhu hoho");
    
    assertThat(nsToMs(profiler.getLexerCpuTime()), lessThan(50L));
    
    assertThat(nsToMs(profiler.getParserCpuTime()), greaterThan(300L));
    assertThat(nsToMs(profiler.getParserCpuTime()), lessThan(700L));
    
    assertThat(profiler.getHits(), equalTo(2L));
    
    assertThat(profiler.getBacktracks(), equalTo(1L));
    assertThat(nsToMs(profiler.getAverageBacktrackCpuTime()), greaterThan(300.0));
    assertThat(nsToMs(profiler.getAverageBacktrackCpuTime()), lessThan(700.0));
    assertThat(nsToMs(profiler.getMaxBacktrackCpuTime()), greaterThan(300L));
    assertThat(nsToMs(profiler.getMaxBacktrackCpuTime()), lessThan(700L));
    assertThat(profiler.getAverageLookahead(), equalTo(4.0));
    assertThat(profiler.getMaxLookahead(), equalTo(4));
    
    assertThat(profiler.getMemoizedHits(), equalTo(0L));
    assertThat(profiler.getMemoizedMisses(), equalTo(2L));
    
    assertThat(nsToMs(profiler.getTotalNonMemoizedHitCpuTime()), greaterThan(300L));
    assertThat(nsToMs(profiler.getTotalNonMemoizedHitCpuTime()), lessThan(700L));
    
    for (Map.Entry<RuleMatcher, Profiler.RuleCounter> entry: profiler.ruleStats.entrySet()) {
    	Profiler.RuleCounter counter = entry.getValue();
    	
    	if (entry.getKey().getName().equals("rule1")) {
    		assertThat(counter.getHits(), equalTo(1));
    		
    		assertThat(counter.getBacktracks(), equalTo(1));
    		assertThat(nsToMs(counter.getAverageBacktracksCpuTime()), greaterThan(300.0));
    		assertThat(nsToMs(counter.getAverageBacktracksCpuTime()), lessThan(700.0));
    		assertThat(nsToMs(counter.getMaxBacktrackCpuTime()), greaterThan(300L));
    		assertThat(nsToMs(counter.getMaxBacktrackCpuTime()), lessThan(700L));
    		assertThat(counter.getAverageLookahead(), equalTo(4.0));
    		assertThat(counter.getMaxLookahead(), equalTo(4));
    		
    		assertThat(counter.getMemoizedHits(), equalTo(0));
    		assertThat(counter.getMemoizedMisses(), equalTo(1));
    		
        assertThat(nsToMs(counter.getTotalNonMemoizedHitCpuTime()), greaterThan(300L));
        assertThat(nsToMs(counter.getTotalNonMemoizedHitCpuTime()), lessThan(700L));
    	} else if (entry.getKey().getName().equals("root")) {
    		assertThat(counter.getHits(), equalTo(1));
    		
    		assertThat(counter.getBacktracks(), equalTo(0));
    		assertThat(counter.getAverageBacktracksCpuTime(), equalTo(0.0));
    		assertThat(counter.getMaxBacktrackCpuTime(), equalTo(0L));
    		assertThat(counter.getAverageLookahead(), equalTo(0.0));
    		assertThat(counter.getMaxLookahead(), equalTo(0));
    		
    		assertThat(counter.getMemoizedHits(), equalTo(0));
    		assertThat(counter.getMemoizedMisses(), equalTo(1));
    		
        assertThat(nsToMs(counter.getTotalNonMemoizedHitCpuTime()), lessThan(50L));
    	} else {
    		throw new IllegalStateException();
    	}
    }
    
  }

}
