/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.miniC.MiniCLexer.Punctuators.*;

import java.util.List;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Preprocessor;
import com.sonar.sslr.api.Token;

public class MiniCPreprocessor extends Preprocessor {

  private final List<Token> buffer = Lists.newLinkedList();

  @Override
  public void startLexing() {
    buffer.clear();
  }

  private boolean isBufferValid() {
    /* Launch the preprocessor parser on buffer, and set the resulting AstNode on the first token */

    return buffer.size() == 3
        && buffer.get(0).getType() == HASH
        && buffer.get(1).getType() == IDENTIFIER
        && "define".equals(buffer.get(1).getOriginalValue())
        && buffer.get(2).getType() == IDENTIFIER;
  }

  private void preprocessBuffer(LexerOutput output) {
    /* Push the preprocessed token */
    for (Token preprocessedToken : buffer) {
      output.addPreprocessingToken(preprocessedToken);
    }
  }

  @Override
  public boolean process(Token token, LexerOutput output) {
    if ( !buffer.isEmpty() && (token.getType() == EOF || token.getLine() != buffer.get(0).getLine())) {
      if (isBufferValid()) {
        preprocessBuffer(output);
      } else {
        output.pushBackTokensAndProcess(buffer, this);
      }
      buffer.clear();
    }

    if (buffer.isEmpty()) {
      if (token.getType() == HASH) {
        buffer.add(token);
        return true;
      }

      return false;
    } else {
      buffer.add(token);
      return true;
    }
  }

}
