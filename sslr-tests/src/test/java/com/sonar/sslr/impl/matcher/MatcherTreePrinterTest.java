/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.junit.Assert.*;

public class MatcherTreePrinterTest {

  @Test
  public void testPrint() {
    assertEquals(MatcherTreePrinter.print(or("test")), "\"test\"");
    assertEquals(MatcherTreePrinter.print(and("a", "b")), "and(\"a\", \"b\")");
    assertEquals(MatcherTreePrinter.print(o2n("a")), "opt(one2n(\"a\"))");
    assertEquals(MatcherTreePrinter.print(o2n("a", "b")), "opt(one2n(and(\"a\", \"b\")))");

    RuleDefinition heheBuilder = RuleDefinition.newRuleBuilder("hehe");
    RuleMatcher hehe = heheBuilder.is("bonjour", heheBuilder).getRule();
    assertEquals(MatcherTreePrinter.print(hehe), "hehe.is(and(\"bonjour\", hehe))");
  }

}
