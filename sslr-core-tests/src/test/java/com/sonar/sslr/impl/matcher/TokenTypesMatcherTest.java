/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.MockTokenType;

public class TokenTypesMatcherTest {

  @Test
  public void ok() {
    TokenTypesMatcher matcher = new TokenTypesMatcher(MockTokenType.values());
    assertTrue(matcher.isExpectedToken(new Token(MockTokenType.WORD2, "word2")));

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

    assertFalse(matcher.isExpectedToken(new Token(dummyTokenType, "word2")));
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
