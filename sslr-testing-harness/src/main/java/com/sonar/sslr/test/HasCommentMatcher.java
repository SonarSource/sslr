/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.LexerOutput;

class HasCommentMatcher extends BaseMatcher<LexerOutput> {

  private final String commentValue;

  HasCommentMatcher(String commentValue) {
    this.commentValue = commentValue;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof LexerOutput)) {
      return false;
    }
    LexerOutput output = (LexerOutput) obj;
    for (Token comment : output.getCommentTokens().values()) {
      if (comment.getValue().equals(commentValue)) {
        return true;
      }
    }
    return false;
  }

  public void describeTo(Description desc) {
    desc.appendText("Comment('" + commentValue + "')");
  }
}
