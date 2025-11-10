/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.test.lexer;

import com.sonar.sslr.api.Token;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;

class HasTokensMatcher extends BaseMatcher<List<Token>> {

  private final String[] tokenValues;

  HasTokensMatcher(String... tokenValues) {
    this.tokenValues = tokenValues;
  }

  @Override
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

  @Override
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
