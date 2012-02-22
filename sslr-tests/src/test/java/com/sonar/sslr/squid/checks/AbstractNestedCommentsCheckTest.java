/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

public class AbstractNestedCommentsCheckTest {

  private static class NestedCommentsCheck extends AbstractNestedCommentsCheck<MiniCGrammar> {

    private static final String[] COMMENT_START_TAGS = new String[] {"/*", "//"};

    @Override
    public String[] getCommentStartTags() {
      return COMMENT_START_TAGS;
    }

  }

  @Test
  public void singleLineCommentsSyntax() {
    setCurrentSourceFile(scanFile("/checks/nested_comments.mc", new NestedCommentsCheck()));

    assertNumberOfViolations(2);

    assertViolation().atLine(1).withMessage("This comments contains the nested comment start tag \"/*\"");
    assertViolation().atLine(2).withMessage("This comments contains the nested comment start tag \"//\"");
  }

}
