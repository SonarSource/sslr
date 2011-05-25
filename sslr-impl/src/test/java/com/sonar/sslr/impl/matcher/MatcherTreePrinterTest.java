/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.Matchers.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sonar.sslr.impl.events.MatcherAdapter;
import com.sonar.sslr.impl.events.RuleImplAdapter;

public class MatcherTreePrinterTest {

  @Test
  public void testPrint() {
  	assertEquals(MatcherTreePrinter.print(or("test")), "or(\"test\")");
    assertEquals(MatcherTreePrinter.print(and("a", "b")), "and(\"a\", \"b\")");
    assertEquals(MatcherTreePrinter.print(o2n("a")), "o2n(\"a\")");
    assertEquals(MatcherTreePrinter.print(o2n("a", "b")), "o2n(and(\"a\", \"b\"))");
    
    RuleImpl hehe = new RuleImpl("hehe");
    hehe.is("bonjour", hehe);
    assertEquals(MatcherTreePrinter.print(hehe), "hehe.is(and(\"bonjour\", hehe))");
    
  	RuleImpl haha = new RuleImpl("haha");
  	haha.is(new MemoizerMatcher(new MatcherAdapter(null, or("a", "b"))));

  	RuleImplAdapter adapter = new RuleImplAdapter(null, haha);

    assertEquals(MatcherTreePrinter.print(adapter), "haha.is(or(\"a\", \"b\"))");
    assertEquals(MatcherTreePrinter.print(adapter.getRuleImpl()), "haha.is(or(\"a\", \"b\"))");
  }
  
  @Test
  public void testPrintWithAdapters() {
  	RuleImpl haha = new RuleImpl("haha");
  	haha.is(new MemoizerMatcher(new MatcherAdapter(null, or("a", "b"))));

  	RuleImplAdapter adapter = new RuleImplAdapter(null, haha);

    assertEquals(MatcherTreePrinter.printWithAdapters(adapter), "RuleImplAdapter(haha)");
    assertEquals(MatcherTreePrinter.printWithAdapters(adapter.getRuleImpl()), "haha.is(MemoizerMatcher(MatcherAdapter(or(\"a\", \"b\"))))");
  }

}
