/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

import org.junit.Test;
import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.test.miniC.MiniCAstScanner.MiniCMetrics;
import com.sonar.sslr.test.miniC.MiniCGrammar;

public class AbstractFileComplexityCheckTest {

  private static class FileComplexityCheck extends AbstractFileComplexityCheck<MiniCGrammar> {

    @Override
    public MetricDef getComplexityMetric() {
      return MiniCMetrics.COMPLEXITY;
    }

  }

  @Test
  public void fileComplexityEqualsMaximum() {
    FileComplexityCheck check = new FileComplexityCheck();
    check.maximumFileComplexity = 5;

    setCurrentSourceFile(scanFile("/checks/complexity5.mc", check));

    assertNoViolation();
  }

  @Test
  public void fileComplexityGreaterMaximum() {
    FileComplexityCheck check = new FileComplexityCheck();
    check.maximumFileComplexity = 4;

    setCurrentSourceFile(scanFile("/checks/complexity5.mc", check));

    assertNumberOfViolations(1);
    assertViolation().withMessage("The file is too complex (5 while maximum allowed is set to 4).");
  }

}
