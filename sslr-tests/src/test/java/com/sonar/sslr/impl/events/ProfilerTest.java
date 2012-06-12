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

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.impl.events.DelayMatcher.*;
import static com.sonar.sslr.impl.events.ProfilerStream.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.junit.Test;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.matcher.RuleMatcher;

@org.junit.Ignore("Broken due to refactorings")
public class ProfilerTest {

  Profiler profiler = new Profiler();

  public class MyTestGrammar extends Grammar {

    public Rule root;
    public Rule rule1;

    @Override
    public Rule getRootRule() {
      return root;
    }

  }

  public Parser<MyTestGrammar> createParser(MyTestGrammar myTestGrammarImpl) {
    return Parser.builder(myTestGrammarImpl).withLexer(IdentifierLexer.create()).setParsingEventListeners(profiler).build();
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

    for (Map.Entry<RuleMatcher, Profiler.RuleCounter> entry : profiler.ruleStats.entrySet()) {
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

    for (Map.Entry<RuleMatcher, Profiler.RuleCounter> entry : profiler.ruleStats.entrySet()) {
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
