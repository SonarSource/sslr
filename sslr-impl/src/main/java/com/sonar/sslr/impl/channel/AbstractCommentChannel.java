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
import com.sonar.sslr.impl.LexerOutput;

public abstract class AbstractCommentChannel implements Channel<LexerOutput> {

  private final char[] starter;

  public AbstractCommentChannel(String starter) {
    this.starter = starter.toCharArray();
  }

  public final boolean consum(CodeReader code, LexerOutput output) {
    if (code.peek() == starter[0] && isCommentStarting(code)) {
      StringBuilder comment = new StringBuilder();
      code.popTo(getEndCommentMatcher(), comment);
      output.addToken(GenericTokenType.COMMENT, comment.toString(), code.getLinePosition(), code.getColumnPosition());
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
