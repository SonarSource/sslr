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
package org.sonar.sslr.examples.recursion;

import com.sonar.sslr.api.Grammar;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.parser.ParseRunner;

public class LeftRecursiveGrammarTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Grammar grammar = LeftRecursiveGrammar.create();

  @Test
  public void should_detect_immediate_left_recursion() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("Left recursion has been detected, involved rule: " + LeftRecursiveGrammar.A);
    new ParseRunner(grammar.rule(LeftRecursiveGrammar.A)).parse("".toCharArray());
  }

  @Test
  public void should_detect_indirect_left_recursion() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("Left recursion has been detected, involved rule: " + LeftRecursiveGrammar.C);
    new ParseRunner(grammar.rule(LeftRecursiveGrammar.B)).parse("".toCharArray());
  }

}
