/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

public class AbstractGotoCheckTest {

  private static class Check extends AbstractGotoCheck<MiniCGrammar> {

    @Override
    public Rule getGotoRule() {
      return getContext().getGrammar().breakStatement;
    }

  }

  @Test
  public void detected() {
    setCurrentSourceFile(scanFile("/checks/goto.mc", new Check()));

    assertOnlyOneViolation().atLine(9).withMessage("Goto should be avoided.");
  }
}
