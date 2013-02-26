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

import org.junit.Ignore;
import org.junit.Test;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static org.fest.assertions.Assertions.assertThat;

@Ignore("MatcherTreePrinter is broken")
public class MatcherTreePrinterTest {

  @Test
  public void testPrint() {
    assertThat(MatcherTreePrinter.print(or("test"))).isEqualTo("\"test\"");
    assertThat(MatcherTreePrinter.print(and("a", "b"))).isEqualTo("and(\"a\", \"b\")");
    assertThat(MatcherTreePrinter.print(o2n("a"))).isEqualTo("opt(one2n(\"a\"))");
    assertThat(MatcherTreePrinter.print(o2n("a", "b"))).isEqualTo("opt(one2n(and(\"a\", \"b\")))");

    RuleDefinition heheBuilder = RuleDefinition.newRuleBuilder("hehe");
    RuleMatcher hehe = heheBuilder.is("bonjour", heheBuilder).getRule();
    assertThat(MatcherTreePrinter.print(hehe)).isEqualTo("hehe.is(and(\"bonjour\", hehe))");
  }

}
