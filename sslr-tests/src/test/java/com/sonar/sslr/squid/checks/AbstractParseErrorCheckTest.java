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

public class AbstractParseErrorCheckTest {

  private static class Check extends AbstractParseErrorCheck<MiniCGrammar> {
  }

  @Test
  public void parseError() {
    setCurrentSourceFile(scanFile("/checks/parse_error.mc", new Check()));

    assertOnlyOneViolation().atLine(3);
  }

}
