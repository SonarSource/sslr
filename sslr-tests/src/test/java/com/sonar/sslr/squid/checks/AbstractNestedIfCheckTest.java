/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

import org.junit.Test;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.test.miniC.MiniCGrammar;

public class AbstractNestedIfCheckTest {

  private static class NestedIfCheck extends AbstractNestedIfCheck<MiniCGrammar> {

    public int maximumNestingLevel = 3;

    @Override
    public int getMaximumNestingLevel() {
      return maximumNestingLevel;
    }

    @Override
    public Rule getIfRule() {
      return getContext().getGrammar().ifStatement;
    }

  }

  @Test
  public void nestedIfWithDefaultNesting() {
    setCurrentSourceFile(scanFile("/checks/nested_if.mc", new NestedIfCheck()));

    assertOnlyOneViolation().atLine(9).withMessage("This if has a nesting level of 4, which is higher than the maximum allowed 3.");
  }

  @Test
  public void nestedIfWithSpecificNesting() {
    NestedIfCheck check = new NestedIfCheck();
    check.maximumNestingLevel = 2;

    setCurrentSourceFile(scanFile("/checks/nested_if.mc", check));

    assertNumberOfViolations(3);

    assertViolation().atLine(7);
    assertViolation().atLine(9);
    assertViolation().atLine(27);
  }

}
