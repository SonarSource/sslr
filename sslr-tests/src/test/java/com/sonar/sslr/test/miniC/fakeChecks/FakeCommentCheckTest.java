/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC.fakeChecks;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

import org.junit.Test;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.squid.checks.SquidCheck;
import com.sonar.sslr.test.miniC.MiniCGrammar;

public class FakeCommentCheckTest {

  private class FakeCommentCheck extends SquidCheck<MiniCGrammar> implements AstAndTokenVisitor {

    public void visitToken(Token token) {
      for (Trivia trivia : token.getTrivia()) {
        if (trivia.isComment() && trivia.getValue().contains("stupid")) {
          getContext().log(this, "Be gentle in your comments.", trivia.getLine());
        }
      }
    }

  }

  @Test
  public void testFakeCommentCheck() {
    setCurrentSourceFile(scanFile("/fakeChecks/fakeComment.mc", new FakeCommentCheck()));

    assertOnlyOneViolation().atLine(6);
  }

}
