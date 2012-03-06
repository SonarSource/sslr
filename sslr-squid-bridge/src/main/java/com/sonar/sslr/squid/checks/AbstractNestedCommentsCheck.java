/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

import java.util.Set;

public abstract class AbstractNestedCommentsCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AstAndTokenVisitor {

  public abstract Set<String> getCommentStartTags();

  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      if (trivia.isComment()) {
        String contents = getContext().getCommentAnalyser().getContents(trivia.getToken().getOriginalValue());

        for (String commentStartTag : getCommentStartTags()) {
          if (contents.contains(commentStartTag)) {
            getContext().createLineViolation(this, "This comments contains the nested comment start tag \"{0}\"", trivia.getToken(), commentStartTag);
            break;
          }
        }
      }
    }
  }

}
