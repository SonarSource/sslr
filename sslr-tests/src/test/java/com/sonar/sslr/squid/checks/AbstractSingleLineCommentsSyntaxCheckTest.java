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

public class AbstractSingleLineCommentsSyntaxCheckTest {

  private static class Check extends AbstractSingleLineCommentsSyntaxCheck<MiniCGrammar> {

    @Override
    public String getSingleLineCommentSyntaxPrefix() {
      return "//";
    }

  }

  @Test
  public void singleLineCommentsSyntax() {
    setCurrentSourceFile(scanFile("/checks/single_line_comments_syntax.mc", new Check()));

    assertNumberOfViolations(2);

    assertViolation().atLine(1).withMessage("This single line comment should use the single line comment syntax \"//\"");
    assertViolation().atLine(15);
  }

}
