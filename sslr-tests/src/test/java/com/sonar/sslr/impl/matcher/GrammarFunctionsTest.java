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

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.MockTokenType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.sonar.sslr.api.GenericTokenType.COMMENT;
import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.EOL;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.adjacent;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.anyToken;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.anyTokenButNot;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.bridge;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.exclusiveTill;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.isFalse;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.isOneOfThem;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.isTrue;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.till;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.tillNewLine;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.next;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.MyPunctuator.LEFT;
import static com.sonar.sslr.impl.matcher.MyPunctuator.RIGHT;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class GrammarFunctionsTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void convertToMatcher_should_check_type() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The matcher object can't be anything else than a Rule, Matcher, String, TokenType or Class. Object = 1");
    GrammarFunctions.convertToMatcher(Integer.valueOf(1));
  }

  @Test
  public void convertToMatcher_should_not_accept_null() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Null is not a valid matcher.");
    GrammarFunctions.convertToMatcher(null);
  }

  @Test
  public void testCacheAndEqualsAndHashCode() {
    assertThat(and(opt(or(EOF, EOL)), IDENTIFIER) == and(opt(or(EOF, EOL)), IDENTIFIER), is(true));
    assertThat(
        and("LOOP", one2n("statement"), "END", "LOOP", opt(IDENTIFIER), "SEMICOLON") == and("LOOP", one2n("statement"), "END", "LOOP",
            opt(IDENTIFIER), "SEMICOLON"), is(true));
    assertThat(
        and(or("hehe", "huhu", and("haha", opt("hoho"))), IDENTIFIER,
            next("hehe", com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not(and("hmmm"))), GenericTokenType.class,
            longestOne("one", "two", "three")) == and(or("hehe", "huhu", and("haha", opt("hoho"))),
            IDENTIFIER, next("hehe", com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not(and("hmmm"))), GenericTokenType.class,
            longestOne("one", "two", "three")), is(true));
  }

  @Test
  public void test_and() {
    assertThat(and("(").toString()).isEqualTo("\"(\"");
    assertThat(and("(", ")").toString()).isEqualTo("and");

    assertThat(and("a", "a") == and("a", "a")).isTrue();
    assertThat(and("a", "a") == and("a", "b")).isFalse();
    assertThat(and("a", "a") == longestOne("a", "a")).isFalse();
  }

  @Test
  public void test_or() {
    assertThat(or("(").toString()).isEqualTo("\"(\"");
    assertThat(or("(", ")").toString()).isEqualTo("or");

    assertThat(or("a", "a") == or("a", "a")).isTrue();
    assertThat(or("a", "a") == or("a", "b")).isFalse();
    assertThat(or("a", "a") == longestOne("a", "a")).isFalse();
  }

  @Test
  public void test_one2n() {
    assertThat(one2n("(").toString()).isEqualTo("one2n");

    assertThat(one2n("a", "a") == one2n("a", "a")).isTrue();
    assertThat(one2n("a", "a") == one2n("a", "b")).isFalse();
    assertThat(one2n("a", "a") == longestOne("a", "a")).isFalse();
  }

  @Test
  public void test_opt() {
    assertThat(opt("(").toString()).isEqualTo("opt");

    assertThat(opt("a", "a") == opt("a", "a")).isTrue();
    assertThat(opt("a", "a") == opt("a", "b")).isFalse();
    assertThat(opt("a", "a") == longestOne("a", "a")).isFalse();
  }

  @Test
  public void test_not() {
    assertThat(GrammarFunctions.Predicate.not("(").toString()).isEqualTo("not");

    assertThat(GrammarFunctions.Predicate.not("a") == GrammarFunctions.Predicate.not("a")).isTrue();
    assertThat(GrammarFunctions.Predicate.not("a") == GrammarFunctions.Predicate.not("b")).isFalse();
    assertThat(GrammarFunctions.Predicate.not("a") == GrammarFunctions.Predicate.next("a")).isFalse();

    assertThat(and("one", GrammarFunctions.Predicate.not("two"), "three"), match("one three"));
    assertThat(and("one", GrammarFunctions.Predicate.not("two"), "two"),
        org.hamcrest.Matchers.not(match("one two")));

    assertThat(and(opt(GrammarFunctions.Predicate.not(and("one", "two")), "one"), opt(and("one", "two"))),
        match("one"));
    assertThat(and(opt(GrammarFunctions.Predicate.not(and("one", "two")), "one"), opt(and("one", "two"))),
        match("one two"));
  }

  @Test
  public void test_next() {
    assertThat(next("(").toString()).isEqualTo("next");

    assertThat(next("a", "a") == next("a", "a")).isTrue();
    assertThat(next("a", "a") == next("a", "b")).isFalse();
    assertThat(next("a", "a") == and("a", "a")).isFalse();

    assertThat(and(next("one"), "one"), match("one"));
    assertThat(and(next("two"), "one"), not(match("one")));
  }

  @Test
  public void test_boolean() {
    assertThat(isTrue().toString()).isEqualTo("isTrue()");
    assertThat(isFalse().toString()).isEqualTo("isFalse()");

    assertThat(isTrue() == isTrue()).isTrue();
    assertThat(isFalse() == isFalse()).isTrue();
    assertThat(isTrue() == isFalse()).isFalse();
    assertThat(anyToken() == tillNewLine()).isFalse();
  }

  @Test
  public void test_anyToken() {
    assertThat(anyToken().toString()).isEqualTo("anyToken()");

    assertThat(anyToken() == anyToken()).isTrue();
    assertThat(anyToken() == tillNewLine()).isFalse();
  }

  @Test
  public void test_anyTokenButNot() {
    assertThat(anyTokenButNot("(").toString()).isEqualTo("anyTokenButNot");

    assertThat(anyTokenButNot("a") == anyTokenButNot("a")).isTrue();
    assertThat(anyTokenButNot("a") == anyTokenButNot("b")).isFalse();
    assertThat(anyTokenButNot("a") == adjacent("a")).isFalse();
  }

  @Test
  public void test_till() {
    assertThat(till("(").toString()).isEqualTo("till");

    assertThat(till("a") == till("a")).isTrue();
    assertThat(till("a") == till("b")).isFalse();
    assertThat(till("a") == adjacent("a")).isFalse();
  }

  @Test
  public void test_exclusiveTill() {
    assertThat(exclusiveTill("(").toString()).isEqualTo("exclusiveTill");

    assertThat(exclusiveTill("a", "a") == exclusiveTill("a", "a")).isTrue();
    assertThat(exclusiveTill("a", "a") == exclusiveTill("a", "b")).isFalse();
    assertThat(exclusiveTill("a", "a") == adjacent("a")).isFalse();
  }

  @Test
  public void test_tokenValue() {
    assertThat(and("hehe") == and("hehe")).isTrue();
    assertThat(and("hehe") == and("haha")).isFalse();
    assertThat(and("hehe") == adjacent("hehe")).isFalse();
  }

  @Test
  public void test_tokenType() {
    assertThat(and(IDENTIFIER).toString()).isEqualTo("IDENTIFIER");

    assertThat(and(IDENTIFIER) == and(IDENTIFIER)).isTrue();
    assertThat(and(IDENTIFIER) == and(EOF)).isFalse();
    assertThat(and(IDENTIFIER) == adjacent("(")).isFalse();
  }

  @Test
  public void test_tokenTypeClass() {
    assertThat(and(GenericTokenType.class).toString()).isEqualTo(GenericTokenType.class.getCanonicalName() + ".class");

    assertThat(and(GenericTokenType.class) == and(GenericTokenType.class)).isTrue();
    assertThat(and(GenericTokenType.class) == and(MockTokenType.class)).isFalse();
    assertThat(and(GenericTokenType.class) == adjacent("(")).isFalse();
  }

  @Test
  public void test_tokenTypes() {
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(IDENTIFIER, EOF)).isTrue();
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(EOF, IDENTIFIER)).isTrue();
    assertThat(isOneOfThem(IDENTIFIER, EOF, COMMENT) == isOneOfThem(EOF, COMMENT, IDENTIFIER)).isTrue();
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(IDENTIFIER, LITERAL)).isFalse();
    assertThat(isOneOfThem(IDENTIFIER, EOF) == and(IDENTIFIER, EOF)).isFalse();
  }

  @Test
  public void test_longestOne() {
    assertThat(longestOne("(").toString()).isEqualTo("longestOne");

    assertThat(longestOne("a", "a") == longestOne("a", "a")).isTrue();
    assertThat(longestOne("a", "a") == longestOne("a", "b")).isFalse();
    assertThat(longestOne("a", "a") == and("a", "a")).isFalse();
  }

  @Test
  public void test_adjacent() {
    assertThat(adjacent("(").toString()).isEqualTo("adjacent");

    assertThat(adjacent("a") == adjacent("a")).isTrue();
    assertThat(adjacent("a") == adjacent("b")).isFalse();
    assertThat(adjacent("a") == anyTokenButNot("a")).isFalse();
  }

  @Test
  public void test_bridge() {
    assertThat(bridge(LEFT, RIGHT).toString()).isEqualTo("bridge(LEFT, RIGHT)");

    assertThat(bridge(LEFT, RIGHT) == bridge(LEFT, RIGHT)).isTrue();
    assertThat(bridge(LEFT, LEFT) == bridge(LEFT, RIGHT)).isFalse();
    assertThat(bridge(LEFT, LEFT) == and(LEFT, RIGHT)).isFalse();
  }

  @Test
  public void test_tillNewLine() {
    assertThat(tillNewLine().toString()).isEqualTo("tillNewLine()");

    assertThat(tillNewLine() == tillNewLine()).isTrue();
    assertThat(tillNewLine() == anyToken()).isFalse();
  }

}
