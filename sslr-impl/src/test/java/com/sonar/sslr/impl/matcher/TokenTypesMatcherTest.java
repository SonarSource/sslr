/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.MockTokenType;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenTypesMatcherTest {

  @Test
  public void testIsExpectedToken() {
    TokenTypesMatcher matcher = new TokenTypesMatcher(MockTokenType.values());
    assertTrue(matcher.isExpectedToken(new Token(MockTokenType.WORD2, "word2")));
    
    TokenType dummyTokenType = new TokenType() {
      
      public boolean hasToBeSkippedFromAst() {
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
  
}
