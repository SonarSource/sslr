/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import org.sonar.channel.EndMatcher;

public class InlineCommentChannel extends AbstractCommentChannel {
  
  public InlineCommentChannel(String starter) {
    super(starter);
  }

  @Override
  protected EndMatcher getEndCommentMatcher() {
    return new EndMatcher() {

      public boolean match(int toMatch) {
        return toMatch == '\n' || toMatch == '\r';
      }};
  }
}
