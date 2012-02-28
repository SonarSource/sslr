/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import com.sonar.sslr.test.miniC.MiniCAstScanner.MiniCMetrics;
import org.junit.Test;
import org.sonar.squid.api.SourceFile;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ComplexityVisitorTest {

  @Test
  public void counter() {
    SourceFile sourceFile = scanFile("/metrics/complexity.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.COMPLEXITY), is(4));
    assertThat(sourceFile.getInt(MiniCMetrics.STATEMENTS), is(11));
    assertThat(sourceFile.getInt(MiniCMetrics.FUNCTIONS), is(2));
  }

}
