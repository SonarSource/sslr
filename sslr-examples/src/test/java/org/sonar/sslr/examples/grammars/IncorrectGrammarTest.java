/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.parser.ParseRunner;

import java.util.regex.PatternSyntaxException;

public class IncorrectGrammarTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void undefined_rule() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule 'A' hasn't beed defined.");
    IncorrectGrammar.undefinedRule();
  }

  @Test
  public void reference_to_undefined_rule() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule 'B' hasn't beed defined.");
    IncorrectGrammar.referenceToUndefinedRule();
  }

  @Test
  public void rule_defined_twice() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule 'A' has already been defined somewhere in the grammar.");
    IncorrectGrammar.ruleDefinedTwice();
  }

  @Test
  public void incorrect_regular_expression() {
    thrown.expect(PatternSyntaxException.class);
    thrown.expectMessage("Dangling meta character '*' near index 0");
    IncorrectGrammar.incorrectRegularExpression();
  }

  @Test
  public void infinite_zero_or_more_expression() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The inner part of ZeroOrMore and OneOrMore must not allow empty matches");
    new ParseRunner(IncorrectGrammar.infiniteZeroOrMore().rule(IncorrectGrammar.A)).parse("foo".toCharArray());
  }

  @Test
  public void infinite_one_or_more_expression() {
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The inner part of ZeroOrMore and OneOrMore must not allow empty matches");
    new ParseRunner(IncorrectGrammar.infiniteOneOrMore().rule(IncorrectGrammar.A)).parse("foo".toCharArray());
  }

}
