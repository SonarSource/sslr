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
package org.sonar.sslr.tests;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.RegexpChannel;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ParserAssertTest {

  private Rule rule;
  private Parser parser;

  @Before
  public void setUp() {
    Lexer lexer = Lexer.builder()
        .withFailIfNoChannelToConsumeOneCharacter(true)
        .withChannel(new RegexpChannel(GenericTokenType.IDENTIFIER, "[a-z]++"))
        .withChannel(new BlackHoleChannel(" "))
        .build();
    rule = new RuleDefinition("ruleName").is("foo");
    Grammar grammar = new Grammar() {
      @Override
      public Rule getRootRule() {
        return rule;
      }
    };
    parser = Parser.builder(grammar).withLexer(lexer).build();
  }

  @Test
  public void ok() {
    new ParserAssert(parser)
        .matches("foo")
        .notMatches("bar")
        .notMatches("foo foo");
  }

  @Test
  public void test_matches_failure() {
    ParsingResultComparisonFailure thrown = assertThrows(ParsingResultComparisonFailure.class,
      () -> new ParserAssert(parser).matches("bar"));
    assertTrue(thrown.getMessage().contains("Rule 'ruleName' should match:\nbar"));
  }

  @Test
  public void test2_matches_failure() {
    ParsingResultComparisonFailure thrown = assertThrows(ParsingResultComparisonFailure.class,
      () -> new ParserAssert(parser).matches("foo bar"));
    assertTrue(thrown.getMessage().contains("Rule 'ruleName' should match:\nfoo bar"));
  }

  @Test
  public void test_notMatches_failure() {
    AssertionError thrown = assertThrows(AssertionError.class,
      () -> new ParserAssert(parser).notMatches("foo"));
    assertEquals("Rule 'ruleName' should not match:\nfoo", thrown.getMessage());
  }

  @Test
  public void test_notMatches_failure2() {
    rule.override("foo", GenericTokenType.EOF);
    AssertionError thrown = assertThrows(AssertionError.class,
      () -> new ParserAssert(parser).notMatches("foo"));
    assertEquals("Rule 'ruleName' should not match:\nfoo", thrown.getMessage());
  }

  @Test
  public void should_not_accept_null() {
    AssertionError thrown = assertThrows(AssertionError.class,
      () -> new ParserAssert((Parser) null).matches(""));
    assertEquals("expecting actual value not to be null", thrown.getMessage());
  }

  @Test
  public void should_not_accept_null_root_rule() {
    parser.setRootRule(null);
    AssertionError thrown = assertThrows(AssertionError.class,
      () -> new ParserAssert(parser).matches(""));
    assertEquals("Root rule of the parser should not be null", thrown.getMessage());
  }

  @Test
  public void test_lexer_failure() {
    ParsingResultComparisonFailure thrown = assertThrows(ParsingResultComparisonFailure.class,
      () -> new ParserAssert(parser).matches("_"));
    String expectedMessage = new StringBuilder()
        .append("Rule 'ruleName' should match:\n")
        .append("_\n")
        .append("Lexer error: Unable to lex")
        .toString();
    assertTrue(thrown.getMessage().contains(expectedMessage));
  }

}
