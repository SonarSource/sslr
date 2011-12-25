/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.sonar.squid.api.SourceFile;

import com.sonar.sslr.test.miniC.MiniCAstScanner.MiniCMetrics;

public class LinesOfCodeVisitorTest {

  @Test
  public void linesOfCode() {
    SourceFile sourceFile = scanFile("/metrics/lines_of_code.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.LINES_OF_CODE), is(3));
  }

}
