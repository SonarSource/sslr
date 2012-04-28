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
