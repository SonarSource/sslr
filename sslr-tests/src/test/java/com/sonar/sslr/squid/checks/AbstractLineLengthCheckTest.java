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

public class AbstractLineLengthCheckTest {

  private static class Check extends AbstractLineLengthCheck<MiniCGrammar> {

    public int maximumLineLength = 80;

    @Override
    public int getMaximumLineLength() {
      return maximumLineLength;
    }

  }

  @Test
  public void lineLengthWithDefaultLength() {
    setCurrentSourceFile(scanFile("/checks/line_length.mc", new Check()));

    assertOnlyOneViolation().atLine(3).withMessage("The line length is greater than 80 authorized.");
  }

  @Test
  public void lineLengthWithSpecificLength() {
    Check check = new Check();
    check.maximumLineLength = 7;

    setCurrentSourceFile(scanFile("/checks/line_length.mc", check));

    assertNumberOfViolations(2);

    assertViolation().atLine(3);
    assertViolation().atLine(4);
  }

}
