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

import static com.sonar.sslr.api.GenericTokenType.COMMENT;
import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.isOneOfThem;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.test.lexer.MockHelper.mockToken;
import static org.fest.assertions.Assertions.assertThat;

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
  public void testToString() {
    assertThat(new TokenTypesMatcher(MockTokenType.values()).toString()).isEqualTo("isOneOfThem");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(IDENTIFIER, EOF)).isTrue();
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(EOF, IDENTIFIER)).isTrue();
    assertThat(isOneOfThem(IDENTIFIER, EOF, COMMENT) == isOneOfThem(EOF, COMMENT, IDENTIFIER)).isTrue();
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(IDENTIFIER, LITERAL)).isFalse();
    assertThat(isOneOfThem(IDENTIFIER, EOF) == and(IDENTIFIER, EOF)).isFalse();
  }

}
