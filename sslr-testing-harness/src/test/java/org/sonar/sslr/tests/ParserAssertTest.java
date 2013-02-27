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
import org.junit.rules.ExpectedException;

public class ParserAssertTest {

  @org.junit.Rule
  public ExpectedException thrown = ExpectedException.none();

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
    thrown.expect(ParsingResultComparisonFailure.class);
    thrown.expectMessage("Rule 'ruleName' should match:\nbar");
    new ParserAssert(parser)
        .matches("bar");
  }

  @Test
  public void test2_matches_failure() {
    thrown.expect(ParsingResultComparisonFailure.class);
    thrown.expectMessage("Rule 'ruleName' should match:\nfoo bar");
    new ParserAssert(parser)
        .matches("foo bar");
  }

  @Test
  public void test_notMatches_failure() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Rule 'ruleName' should not match:\nfoo");
    new ParserAssert(parser)
        .notMatches("foo");
  }

  @Test
  public void test_notMatches_failure2() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Rule 'ruleName' should not match:\nfoo");
    rule.override("foo", GenericTokenType.EOF);
    new ParserAssert(parser).notMatches("foo");
  }

  @Test
  public void should_not_accept_null() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("expecting actual value not to be null");
    new ParserAssert((Parser) null).matches("");
  }

  @Test
  public void should_not_accept_null_root_rule() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Root rule of the parser should not be null");
    parser.setRootRule(null);
    new ParserAssert(parser).matches("");
  }

  @Test
  public void test_lexer_failure() {
    thrown.expect(ParsingResultComparisonFailure.class);
    String expectedMessage = new StringBuilder()
        .append("Rule 'ruleName' should match:\n")
        .append("_\n")
        .append("Lexer error: Unable to lex")
        .toString();
    thrown.expectMessage(expectedMessage);
    new ParserAssert(parser).matches("_");
  }

}
