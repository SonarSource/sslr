/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

class HasCommentMatcher extends BaseMatcher<List<Token>> {

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
    if ( !(obj instanceof List)) {
      return false;
    }
    List<Token> tokens = (List<Token>) obj;
    for (Token token : tokens) {
      for (Trivia trivia : token.getTrivia()) {
        if (trivia.isComment()) {
          String value = originalValue ? trivia.getToken().getOriginalValue() : trivia.getToken().getValue();
          if (value.equals(commentValue)) {
            if (commentLine > -1 && trivia.getToken().getLine() != commentLine) {
              continue;
            }
            return true;
          }
        }
      }
    }
    return false;
  }

  public void describeTo(Description desc) {
    if (originalValue) {
      desc.appendText("Comment('" + commentValue + "')");
    } else {
      desc.appendText("OriginalComment('" + commentValue + "')");
    }
  }
}
