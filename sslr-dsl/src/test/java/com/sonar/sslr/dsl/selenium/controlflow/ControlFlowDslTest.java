/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium.controlflow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;

import com.sonar.sslr.dsl.DslRunner;

public class ControlFlowDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  @Ignore
  public void shouldExecuteIfBlock() throws URISyntaxException {
    DslRunner.create(new ControlFlowDsl(), "if true ping endif").inject(output).execute();
    assertThat(output.toString(), is("ping"));
  }
}
