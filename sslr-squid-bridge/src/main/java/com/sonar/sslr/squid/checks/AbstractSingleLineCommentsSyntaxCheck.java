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
import org.apache.commons.lang.StringUtils;

public abstract class AbstractSingleLineCommentsSyntaxCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AstAndTokenVisitor {

  public abstract String getSingleLineCommentSyntaxPrefix();

  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      if (trivia.isComment() && trivia.getToken().getLine() < token.getLine()) {
        String comment = trivia.getToken().getOriginalValue();

        if (!comment.startsWith(getSingleLineCommentSyntaxPrefix()) && !StringUtils.containsAny(comment, "\r\n")) {
          getContext().createLineViolation(this, "This single line comment should use the single line comment syntax \"{0}\"", trivia.getToken(),
              getSingleLineCommentSyntaxPrefix());
        }
      }
    }
  }

}
