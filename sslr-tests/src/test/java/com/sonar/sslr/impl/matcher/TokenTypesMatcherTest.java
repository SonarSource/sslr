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

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.MockTokenType;

public class TokenTypesMatcherTest {

  @Test
  public void ok() {
    TokenTypesMatcher matcher = new TokenTypesMatcher(MockTokenType.values());
    assertTrue(matcher.isExpectedToken(mockToken(MockTokenType.WORD2, "word2")));

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

    assertFalse(matcher.isExpectedToken(mockToken(dummyTokenType, "word2")));
  }

  @Test
  public void testToString() {
    assertEquals(new TokenTypesMatcher(MockTokenType.values()).toString(), "isOneOfThem");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(IDENTIFIER, EOF), is(true));
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(EOF, IDENTIFIER), is(true));
    assertThat(isOneOfThem(IDENTIFIER, EOF, COMMENT) == isOneOfThem(EOF, COMMENT, IDENTIFIER), is(true));
    assertThat(isOneOfThem(IDENTIFIER, EOF) == isOneOfThem(IDENTIFIER, LITERAL), is(false));
    assertThat(isOneOfThem(IDENTIFIER, EOF) == and(IDENTIFIER, EOF), is(false));
  }

}
