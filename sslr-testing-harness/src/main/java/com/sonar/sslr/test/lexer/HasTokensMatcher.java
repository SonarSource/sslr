/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;

class HasTokensMatcher extends BaseMatcher<LexerOutput> {

  private final String[] tokenValues;

  HasTokensMatcher(String... tokenValues) {
    this.tokenValues = tokenValues;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof LexerOutput)) {
      return false;
    }
    LexerOutput output = (LexerOutput) obj;
    for (int i = 0; i < output.getTokens().size(); i++) {
      Token token = output.getTokens().get(i);
      if ( !token.getValue().equals(tokenValues[i])) {
        return false;
      }
    }
    if (tokenValues.length != output.getTokens().size()) {
      return false;
    }
    return true;
  }

  public void describeTo(Description desc) {
    desc.appendText(tokenValues.length + " tokens(");
    for (int i = 0; i < tokenValues.length; i++) {
      desc.appendText("'" + tokenValues[i] + "'");
      if (i < tokenValues.length - 1) {
        desc.appendText(",");
      }
    }
    desc.appendText(")");
  }
}
