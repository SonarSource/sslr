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
import com.sonar.sslr.impl.MockTokenType;
import org.junit.Test;

import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TokenTypesMatcherTest {

  @Test
  public void ok() {
    TokenTypesMatcher matcher = new TokenTypesMatcher(MockTokenType.values());
    assertThat(matcher.isExpectedToken(mockToken(MockTokenType.WORD2, "word2"))).isTrue();

    TokenType dummyTokenType = new TokenType() {

      public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
      }

      public String getValue() {
        return "dummy";
      }

      public String getName() {
        return "dummy";
      }
    };

    assertThat(matcher.isExpectedToken(mockToken(dummyTokenType, "word2"))).isFalse();
  }

  @Test
  public void test_toString() {
    assertThat(new TokenTypesMatcher(mock(TokenType.class), mock(TokenType.class)).toString()).isEqualTo("isOneOfThem");
  }

  @Test
  public void test_equals_and_hashCode() {
    TokenType tokenType = mock(TokenType.class);
    Matcher first = new TokenTypesMatcher(tokenType);
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same type
    Matcher second = new TokenTypesMatcher(tokenType);
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
    // different type
    Matcher third = new TokenTypesMatcher(mock(TokenType.class));
    assertThat(first.equals(third)).isFalse();
  }

}
