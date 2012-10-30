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
import com.sonar.sslr.impl.channel.RegexpChannel;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class ParserAssertTest {

  @org.junit.Rule
  public ExpectedException thrown = ExpectedException.none();

  private Parser parser;

  @Before
  public void setUp() {
    Lexer lexer = Lexer.builder().withChannel(new RegexpChannel(GenericTokenType.IDENTIFIER, "foo")).build();
    Grammar grammar = new Grammar() {
      @Override
      public Rule getRootRule() {
        return RuleDefinition.newRuleBuilder("ruleName").is("foo");
      }
    };
    parser = Parser.builder(grammar).withLexer(lexer).build();
  }

  @Test
  public void ok() {
    assertThat(parser)
        .matches("foo")
        .notMatches("bar")
        .notMatches("foo foo");
  }

  @Test
  public void test_matches_failure() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Rule 'ruleName' should match:\nbar");
    assertThat(parser)
        .matches("bar");
  }

  @Test
  public void test2_matches_failure() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Rule 'ruleName' should match:\nfoo foo");
    assertThat(parser)
        .matches("foo foo");
  }

  @Test
  public void test_notMatches_failure() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Rule 'ruleName' should not match:\nfoo");
    assertThat(parser)
        .notMatches("foo");
  }

  @Test
  public void should_not_accept_null() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("expecting actual value not to be null");
    assertThat((Parser) null);
  }

  @Test
  public void should_not_accept_null_root_rule() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Root rule of the parser should not be null");
    parser.setRootRule(null);
    assertThat(parser).matches("");
  }

}
