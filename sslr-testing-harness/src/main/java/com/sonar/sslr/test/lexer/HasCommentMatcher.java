/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;

class HasCommentMatcher extends BaseMatcher<LexerOutput> {

  private final String commentValue;
  private final int commentLine;
  private final boolean originalValue;

  HasCommentMatcher(String commentValue) {
    this(commentValue, -1);
  }
  
  HasCommentMatcher(String commentValue, boolean originalValue) {
    this(commentValue, -1, originalValue);
  }

  public HasCommentMatcher(String commentValue, int commentLine) {
    this(commentValue, commentLine, false);
  }
  
  public HasCommentMatcher(String commentValue, int commentLine, boolean originalValue) {
    this.commentValue = commentValue;
    this.commentLine = commentLine;
    this.originalValue = originalValue;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof LexerOutput)) {
      return false;
    }
    LexerOutput output = (LexerOutput) obj;
    for (Token comment : output.getCommentTokens()) {
      String value = (originalValue) ? comment.getOriginalValue() : comment.getValue();
      if (value.equals(commentValue)) {
        if (commentLine > -1 && comment.getLine() != commentLine) {
          continue;
        }
        return true;
      }
    }
    return false;
  }

  public void describeTo(Description desc) {
    if (originalValue) desc.appendText("Comment('" + commentValue + "')");
    else desc.appendText("OriginalComment('" + commentValue + "')");
  }
}
