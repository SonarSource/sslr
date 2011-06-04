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

import com.sonar.sslr.dsl.Dsl;

public class ControlFlowDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  public void shouldExecuteConditionBlock() throws URISyntaxException {
    Dsl.builder(new ControlFlowDsl(), "if true ping ping endif").inject(output).compile().execute();
    assertThat(output.toString(), is("pingping"));
  }

  @Test
  public void shouldNotExecuteConditionBlock() throws URISyntaxException {
    Dsl.builder(new ControlFlowDsl(), "if false ping ping ping endif").inject(output).compile().execute();
    assertThat(output.toString(), is(""));
  }

  @Test
  public void shouldExecuteLoopBlock() throws URISyntaxException {
    Dsl.builder(new ControlFlowDsl(), "do 2 times ping enddo").inject(output).compile().execute();
    assertThat(output.toString(), is("pingping"));
  }

  @Test
  public void shouldNotExecuteLoopBlock() throws URISyntaxException {
    Dsl.builder(new ControlFlowDsl(), "do 0 times ping ping ping enddo").inject(output).compile().execute();
    assertThat(output.toString(), is(""));
  }

  @Test
  public void shouldExitFlow() throws URISyntaxException {
    Dsl.builder(new ControlFlowDsl(), "ping exit ping").inject(output).compile().execute();
    assertThat(output.toString(), is("ping"));
  }
}
