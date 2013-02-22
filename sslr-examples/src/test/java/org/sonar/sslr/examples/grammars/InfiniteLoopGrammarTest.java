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
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.Grammar;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.parser.ParseRunner;

public class InfiniteLoopGrammarTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Grammar grammar = InfiniteLoopGrammar.create();

  @Test
  public void zeroOrMore_should_not_cause_infinite_loop() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The inner part of ZeroOrMore must not allow empty matches");
    new ParseRunner(grammar.rule(InfiniteLoopGrammar.A)).parse("foo".toCharArray());
  }

  @Test
  public void oneOrMore_should_not_cause_infinite_loop() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The inner part of ZeroOrMore must not allow empty matches");
    new ParseRunner(grammar.rule(InfiniteLoopGrammar.B)).parse("foo".toCharArray());
  }

}
