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

class HasTokensMatcher extends BaseMatcher<List<Token>> {

  private final String[] tokenValues;

  HasTokensMatcher(String... tokenValues) {
    this.tokenValues = tokenValues;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof List)) {
      return false;
    }
    List<Token> tokens = (List<Token>) obj;
    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      if ( !token.getValue().equals(tokenValues[i])) {
        return false;
      }
    }
    if (tokenValues.length != tokens.size()) {
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
