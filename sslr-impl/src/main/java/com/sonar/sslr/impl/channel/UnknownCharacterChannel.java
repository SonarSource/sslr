/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.channel;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;

public class UnknownCharacterChannel extends Channel<LexerOutput> {

  @Override
  public boolean consume(CodeReader code, LexerOutput lexerOutput) {
    if (code.peek() != -1) {
      lexerOutput.addTokenAndProcess(GenericTokenType.UNKNOWN_CHAR, String.valueOf((char) code.pop()), code.getLinePosition(),
          code.getColumnPosition() - 1);
      return true;
    }
    return false;
  }
}
