/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.test.lexer;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;

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

  @Override
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

  @Override
  public void describeTo(Description desc) {
    if (originalValue) {
      desc.appendText("Comment('" + commentValue + "')");
    } else {
      desc.appendText("OriginalComment('" + commentValue + "')");
    }
  }
}
