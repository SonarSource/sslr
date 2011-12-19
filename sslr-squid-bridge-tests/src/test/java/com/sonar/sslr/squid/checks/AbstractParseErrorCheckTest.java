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

public class AbstractParseErrorCheckTest {

  private static class ParseErrorCheck extends AbstractParseErrorCheck<MiniCGrammar> {
  }

  @Test
  public void parseError() {
    setCurrentSourceFile(scanFile("/checks/parse_error.mc", new ParseErrorCheck()));

    assertOnlyOneViolation().atLine(3);
  }

}
