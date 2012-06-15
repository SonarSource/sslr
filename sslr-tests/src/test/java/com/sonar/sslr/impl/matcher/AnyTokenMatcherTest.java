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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import org.junit.Test;

import static com.sonar.sslr.test.lexer.TokenUtils.lex;
import static org.fest.assertions.Assertions.assertThat;

public class AnyTokenMatcherTest {

  @Test
  public void ok() {
    AnyTokenMatcher matcher = new AnyTokenMatcher();
    AstNode node = matcher.match(new ParsingState(lex("print screen")));
    assertThat(node.getTokenValue()).isEqualTo("print");

    node = matcher.match(new ParsingState(lex(".")));
    assertThat(node.getTokenValue()).isEqualTo(".");
  }

  @Test
  public void testToString() {
    assertThat(new AnyTokenMatcher().toString()).isEqualTo("anyToken()");
  }

  @Test
  public void test_equals_and_hashCode() {
    Matcher first = new AnyTokenMatcher();
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same matcher
    Matcher second = new AnyTokenMatcher();
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
  }

}
