/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import static com.sonar.sslr.api.GenericTokenType.*;

import java.net.URI;
import java.net.URISyntaxException;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;

public final class MockHelper {

  private MockHelper() {
  }

  public static Lexer mockLexer() {
    return Lexer.builder().build();
  }

  public static Token mockToken(TokenType type, String value) {
    try {
      return Token.builder()
          .setType(type)
          .setValueAndOriginalValue(value)
          .setURI(new URI("tests://unittest"))
          .setLine(1)
          .setColumn(1)
          .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static Token.Builder mockTokenBuilder(TokenType type, String value) {
    try {
      return Token.builder()
          .setType(type)
          .setValueAndOriginalValue(value)
          .setURI(new URI("tests://unittest"))
          .setLine(1)
          .setColumn(1);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static AstNode mockAstNode(String name) {
    return new AstNode(GenericTokenType.IDENTIFIER, name, mockToken(LITERAL, "dummy"));
  }

}
