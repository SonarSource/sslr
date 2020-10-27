/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.Grammar;
import org.junit.Test;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.parser.ParseRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.sonar.sslr.tests.Assertions.assertThat;

public class LeftRecursiveGrammarTest {

  @Test
  public void should_detect_immediate_left_recursion() {
    Grammar grammar = LeftRecursiveGrammar.immediateLeftRecursion();
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> new ParseRunner(grammar.rule(LeftRecursiveGrammar.A)).parse("".toCharArray()));
    assertEquals("Left recursion has been detected, involved rule: " + LeftRecursiveGrammar.A, thrown.getMessage());
  }

  @Test
  public void eliminated_immediate_left_recursion() {
    Grammar grammar = LeftRecursiveGrammar.eliminatedImmediateLeftRecursion();
    assertThat(grammar.rule(LeftRecursiveGrammar.A))
      .matches("s1")
      .matches("s2")
      .matches("s1t1")
      .matches("s1t2")
      .matches("s1t1t2")
      .matches("s1t2t1")
      .matches("s2t1")
      .matches("s2t2")
      .matches("s2t1t2")
      .matches("s2t2t1");
  }

  @Test
  public void should_detect_indirect_left_recursion() {
    Grammar grammar = LeftRecursiveGrammar.indirectLeftRecursion();
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> new ParseRunner(grammar.rule(LeftRecursiveGrammar.A)).parse("".toCharArray()));
    assertEquals("Left recursion has been detected, involved rule: " + LeftRecursiveGrammar.B, thrown.getMessage());
  }

  @Test
  public void eliminated_indirect_left_recursion() {
    Grammar grammar = LeftRecursiveGrammar.eliminatedIndirectLeftRecursion();
    assertThat(grammar.rule(LeftRecursiveGrammar.A))
      .matches("s2t1")
      .matches("s2t1t2t1")
      .matches("s2t1t2t1t2t1")
      .matches("s1")
      .matches("s1t2t1")
      .matches("s1t2t1t2t1");
  }

}
