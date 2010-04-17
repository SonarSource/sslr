/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import org.sonar.channel.EndMatcher;

public class MultilineCommentChannel extends AbstractCommentChannel {

  private final char[] ender;
  private final int minimumCommentSize;

  public MultilineCommentChannel(String starter, String ender) {
    super(starter);
    this.ender = ender.toCharArray();
    minimumCommentSize = starter.length() + ender.length();
  }

  @Override
  protected EndMatcher getEndCommentMatcher() {

    return new EndMatcher() {

      private StringBuilder comment = new StringBuilder();
      private int length = 0;

      public boolean match(int nextChar) {
        if (length >= minimumCommentSize && comment.charAt(length - 1) == ender[ender.length - 1]) {
          boolean match = true;
          for (int i = 0; i < ender.length; i++) {
            match = match && comment.charAt(length - 1 - i) == ender[ender.length - 1 - i];
          }
          if (match) {
            return true;
          }
        }
        comment.append((char) nextChar);
        length++;
        return false;
      }
    };
  }

}
