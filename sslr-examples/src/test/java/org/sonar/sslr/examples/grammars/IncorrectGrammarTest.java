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

import org.junit.Test;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.parser.ParseRunner;

import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class IncorrectGrammarTest {

  @Test
  public void undefined_rule() {
    GrammarException thrown = assertThrows(GrammarException.class,
      IncorrectGrammar::undefinedRule);
    assertEquals("The rule 'A' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void reference_to_undefined_rule() {
    GrammarException thrown = assertThrows(GrammarException.class,
      IncorrectGrammar::referenceToUndefinedRule);
    assertEquals("The rule 'B' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void rule_defined_twice() {
    GrammarException thrown = assertThrows(GrammarException.class,
      IncorrectGrammar::ruleDefinedTwice);
    assertEquals("The rule 'A' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

  @Test
  public void incorrect_regular_expression() {
    PatternSyntaxException thrown = assertThrows(PatternSyntaxException.class,
      IncorrectGrammar::incorrectRegularExpression);
    assertTrue(thrown.getMessage().startsWith("Dangling meta character '*' near index 0"));
  }

  @Test
  public void infinite_zero_or_more_expression() {
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> new ParseRunner(IncorrectGrammar.infiniteZeroOrMore().rule(IncorrectGrammar.A)).parse("foo".toCharArray()));
    assertEquals("The inner part of ZeroOrMore and OneOrMore must not allow empty matches", thrown.getMessage());
  }

  @Test
  public void infinite_one_or_more_expression() {
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> new ParseRunner(IncorrectGrammar.infiniteOneOrMore().rule(IncorrectGrammar.A)).parse("foo".toCharArray()));
    assertEquals("The inner part of ZeroOrMore and OneOrMore must not allow empty matches", thrown.getMessage());
  }

}
