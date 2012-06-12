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
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.EOL;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.next;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GrammarFunctionsTest {

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

}
