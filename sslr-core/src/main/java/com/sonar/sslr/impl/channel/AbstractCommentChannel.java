/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;

@Deprecated
public abstract class AbstractCommentChannel extends Channel<LexerOutput> {

  private final char[] starter;

  public AbstractCommentChannel(String starter) {
    this.starter = starter.toCharArray();
  }

  @Override
  public final boolean consume(CodeReader code, LexerOutput output) {
    if (code.peek() == starter[0] && isCommentStarting(code)) {
      StringBuilder comment = new StringBuilder();
      code.popTo(getEndCommentMatcher(), comment);
      output.addCommentToken(new Token(GenericTokenType.COMMENT, comment.toString(), code.getPreviousCursor().getLine(), code
          .getPreviousCursor().getColumn()));
      return true;
    }
    return false;
  }

  private boolean isCommentStarting(CodeReader code) {
    char[] nextChars = code.peek(starter.length);
    boolean commentStart = true;
    for (int i = 0; i < starter.length; i++) {
      commentStart = commentStart && nextChars[i] == starter[i];
    }
    return commentStart;
  }

  protected abstract EndMatcher getEndCommentMatcher();
}
