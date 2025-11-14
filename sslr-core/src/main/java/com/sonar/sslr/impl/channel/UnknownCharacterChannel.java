/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.channel;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import static com.sonar.sslr.api.GenericTokenType.UNKNOWN_CHAR;

/**
 * Creates token with type {@link com.sonar.sslr.api.GenericTokenType#UNKNOWN_CHAR} for any character.
 * This channel, if present, should be the last one.
 *
 * @since 1.2
 */
public class UnknownCharacterChannel extends Channel<Lexer> {

  private final Token.Builder tokenBuilder = Token.builder();

  public UnknownCharacterChannel() {
  }

  /**
   * @deprecated logging removed in 1.20, use {@link #UnknownCharacterChannel()} or implement your own Channel with logging
   */
  @Deprecated
  public UnknownCharacterChannel(boolean shouldLogWarning) {
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    if (code.peek() != -1) {
      char unknownChar = (char) code.pop();

      Token token = tokenBuilder
          .setType(UNKNOWN_CHAR)
          .setValueAndOriginalValue(String.valueOf(unknownChar))
          .setURI(lexer.getURI())
          .setLine(code.getLinePosition())
          .setColumn(code.getColumnPosition() - 1)
          .build();

      lexer.addToken(token);

      return true;
    }
    return false;
  }

}
