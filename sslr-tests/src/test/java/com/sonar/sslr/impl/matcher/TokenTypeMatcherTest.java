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
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ParsingState;
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.test.lexer.TokenUtils.lex;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenTypeMatcherTest {

  @Test
  public void ok() {
    TokenTypeMatcher matcher = new TokenTypeMatcher(IDENTIFIER);
    AstNode node = matcher.match(new ParsingState(lex("print screen")));

    assertThat(node.is(IDENTIFIER)).isTrue();
  }

  @Test
  public void test_toString() {
    TokenType tokenType = mock(TokenType.class);
    when(tokenType.getName()).thenReturn("foo");
    assertThat(new TokenTypeMatcher(tokenType).toString()).isEqualTo("foo");
  }

  @Test
  public void test_equals_and_hashCode() {
    TokenType tokenType = mock(TokenType.class);
    Matcher first = new TokenTypeMatcher(tokenType);
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same type
    Matcher second = new TokenTypeMatcher(tokenType);
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
    // different type
    Matcher third = new TokenTypeMatcher(mock(TokenType.class));
    assertThat(first.equals(third)).isFalse();
  }

}
