/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;

public class UnknownCharacterChannel extends Channel<LexerOutput> {

  private static final Logger LOG = LoggerFactory.getLogger(UnknownCharacterChannel.class);

  private boolean shouldLogWarning = false;

  public UnknownCharacterChannel() {
  }

  public UnknownCharacterChannel(boolean shouldLogWarning) {
    this.shouldLogWarning = true;
  }

  @Override
  public boolean consume(CodeReader code, LexerOutput lexerOutput) {
    if (code.peek() != -1) {
      char unknownChar = (char) code.pop();
      if (shouldLogWarning) {
        LOG.warn("Unknown char: \"" + unknownChar + "\" (" + code.getLinePosition() + ":" + code.getColumnPosition() + ")");
      }
      lexerOutput.addTokenAndProcess(GenericTokenType.UNKNOWN_CHAR, String.valueOf(unknownChar), code.getLinePosition(),
          code.getColumnPosition() - 1);
      return true;
    }
    return false;
  }
}
