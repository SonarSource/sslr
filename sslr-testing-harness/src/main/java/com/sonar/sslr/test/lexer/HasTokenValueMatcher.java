/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.Token;

class HasTokenValueMatcher extends BaseMatcher<List<Token>> {

  private final String tokenValue;
  private final boolean originalValue;

  HasTokenValueMatcher(String tokenValue) {
    this(tokenValue, false);
  }

  HasTokenValueMatcher(String tokenValue, boolean originalValue) {
    this.tokenValue = tokenValue;
    this.originalValue = originalValue;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof List)) {
      return false;
    }
    List<Token> tokens = (List<Token>) obj;
    for (Token token : tokens) {
      String value = originalValue ? token.getOriginalValue() : token.getValue();
      if (value.equals(tokenValue)) {
        return true;
      }
    }
    return false;
  }

  public void describeTo(Description desc) {
    if (originalValue) {
      desc.appendText("OriginalToken('" + tokenValue + "')");
    } else {
      desc.appendText("Token('" + tokenValue + "')");
    }
  }
}
