/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.api.GenericTokenType.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;

public class UnknownCharacterChannel extends Channel<Lexer> {

  private static final Logger LOG = LoggerFactory.getLogger(UnknownCharacterChannel.class);

  // Byte Order Mark that can be found in some Unicode files
  public static final char BOM_CHAR = '\uFEFF';

  private boolean shouldLogWarning = false;

  public UnknownCharacterChannel() {
  }

  public UnknownCharacterChannel(boolean shouldLogWarning) {
    this.shouldLogWarning = shouldLogWarning;
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    if (code.peek() != -1) {
      char unknownChar = (char) code.pop();
      if (unknownChar == BOM_CHAR) {
        return true;
      }
      if (shouldLogWarning) {
        LOG.warn("Unknown char: \"" + unknownChar + "\" (" + lexer.getURI() + ":" + code.getLinePosition() + ":"
            + code.getColumnPosition() + ")");
      }

      Token token = Token.builder()
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
