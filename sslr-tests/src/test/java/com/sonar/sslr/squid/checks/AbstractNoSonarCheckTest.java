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

public class AbstractNoSonarCheckTest {

  private static class Check extends AbstractNoSonarCheck<MiniCGrammar> {
  }

  @Test
  public void singleLineCommentsSyntax() {
    setCurrentSourceFile(scanFile("/checks/no_sonar.mc", new Check()));

    assertNumberOfViolations(3);

    assertViolation().atLine(5).withMessage("Is NOSONAR usage acceptable or does it hide a real quality flaw?");
    assertViolation().atLine(6);
    assertViolation().atLine(10);
  }

}
