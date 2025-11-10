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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;

import java.net.URI;
import java.net.URISyntaxException;

import static com.sonar.sslr.api.GenericTokenType.LITERAL;

/**
 * @deprecated in 1.17 All classes can now be mocked since none of them are final anymore, hence this helper is useless.
 */
@Deprecated
public final class MockHelper {

  private MockHelper() {
  }

  public static Lexer mockLexer() {
    return Lexer.builder().build();
  }

  public static Token mockToken(TokenType type, String value) {
    return mockTokenBuilder(type, value).build();
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
