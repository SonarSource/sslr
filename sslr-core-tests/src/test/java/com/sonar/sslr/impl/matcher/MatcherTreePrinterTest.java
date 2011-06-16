/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.tokenCount;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sonar.sslr.impl.events.MatcherAdapter;
import com.sonar.sslr.impl.events.RuleMatcherAdapter;

public class MatcherTreePrinterTest {

  @Test
  public void testPrint() {
    assertEquals(MatcherTreePrinter.print(or("test")), "or(\"test\")");
    assertEquals(MatcherTreePrinter.print(and("a", "b")), "and(\"a\", \"b\")");
    assertEquals(MatcherTreePrinter.print(o2n("a")), "opt(one2n(\"a\"))");
    assertEquals(MatcherTreePrinter.print(o2n("a", "b")), "opt(one2n(and(\"a\", \"b\")))");
    assertEquals(MatcherTreePrinter.print(tokenCount(TokenCountMatcher.Operator.EQUAL, 1, "hehe")), "tokenCount(TokenCountMatcher.Operator.EQUAL, 1, \"hehe\")");

    RuleDefinition heheBuilder = RuleDefinition.newRuleBuilder("hehe");
    RuleMatcher hehe = heheBuilder.is("bonjour", heheBuilder).getRule();
    assertEquals(MatcherTreePrinter.print(hehe), "hehe.is(and(\"bonjour\", hehe))");

    RuleMatcher haha = RuleDefinition.newRuleBuilder("haha").is(new MemoizerMatcher(new MatcherAdapter(or("a", "b"), null))).getRule();

    RuleMatcherAdapter adapter = new RuleMatcherAdapter(haha, null);

    assertEquals(MatcherTreePrinter.print(adapter), "haha.is(or(\"a\", \"b\"))");
    assertEquals(MatcherTreePrinter.print(adapter.getRuleImpl()), "haha.is(or(\"a\", \"b\"))");
  }

  @Test
  public void testPrintWithAdapters() {
    RuleMatcher haha = RuleDefinition.newRuleBuilder("haha").is(new MemoizerMatcher(new MatcherAdapter(or("a", "b"), null))).getRule();

    RuleMatcherAdapter adapter = new RuleMatcherAdapter(haha, null);

    assertEquals(MatcherTreePrinter.printWithAdapters(adapter), "RuleImplAdapter(haha)");
    assertEquals(MatcherTreePrinter.printWithAdapters(adapter.getRuleImpl()), "haha.is(MemoizerMatcher(MatcherAdapter(or(\"a\", \"b\"))))");
  }

}
