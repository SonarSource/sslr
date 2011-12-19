/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

import org.junit.Test;

import com.sonar.sslr.test.miniC.MiniCGrammar;

public class AbstractLineLengthCheckTest {

  private static class LineLengthCheck extends AbstractLineLengthCheck<MiniCGrammar> {
  }

  @Test
  public void lineLengthWithDefaultLength() {
    setCurrentSourceFile(scanFile("/checks/line_length.mc", new LineLengthCheck()));

    assertOnlyOneViolation().atLine(3).withMessage("The line length is greater than 80 authorized.");
  }

  @Test
  public void lineLengthWithSpecificLength() {
    LineLengthCheck check = new LineLengthCheck();
    check.maximumLineLength = 7;

    setCurrentSourceFile(scanFile("/checks/line_length.mc", check));

    assertNumberOfViolations(2);

    assertViolation().atLine(3);
    assertViolation().atLine(4);
  }

}
