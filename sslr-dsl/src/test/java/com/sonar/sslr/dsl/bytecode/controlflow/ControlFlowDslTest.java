/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode.controlflow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.DslRunner;

public class ControlFlowDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  public void shouldExecuteConditionBlock() throws URISyntaxException {
    DslRunner.create(new ControlFlowDsl(), "if true ping ping endif", output).execute();
    assertThat(output.toString(), is("pingping"));
  }

  @Test
  public void shouldNotExecuteConditionBlock() throws URISyntaxException {
    DslRunner.create(new ControlFlowDsl(), "if false ping ping ping endif", output).execute();
    assertThat(output.toString(), is(""));
  }

  @Test
  public void shouldExecuteLoopBlock() throws URISyntaxException {
    DslRunner.create(new ControlFlowDsl(), "do 2 times ping enddo", output).execute();
    assertThat(output.toString(), is("pingping"));
  }

  @Test
  public void shouldNotExecuteLoopBlock() throws URISyntaxException {
    DslRunner.create(new ControlFlowDsl(), "do 0 times ping ping ping enddo", output).execute();
    assertThat(output.toString(), is(""));
  }

  @Test
  public void shouldExitFlow() throws URISyntaxException {
    DslRunner.create(new ControlFlowDsl(), "ping exit ping", output).execute();
    assertThat(output.toString(), is("ping"));
  }
}
