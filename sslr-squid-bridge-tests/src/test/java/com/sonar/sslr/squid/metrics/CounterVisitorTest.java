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

public class CounterVisitorTest {

  @Test
  public void counter() {
    SourceFile sourceFile = scanFile("/metrics/counter.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.COMPLEXITY), is(4));
    assertThat(sourceFile.getInt(MiniCMetrics.STATEMENTS), is(6));
    assertThat(sourceFile.getInt(MiniCMetrics.FUNCTIONS), is(2));
  }

}
