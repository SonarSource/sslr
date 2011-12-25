/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode.controlflow;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class ControlFlowDslTest {

  StringBuilder output = new StringBuilder();
  Dsl.Builder builder = Dsl.builder().setGrammar(new ControlFlowDsl()).inject(output);

  @Test
  public void shouldExecuteConditionBlock() throws URISyntaxException {
    builder.withSource("if true ping ping endif").compile().execute();
    assertThat(output.toString(), is("pingping"));
  }

  @Test
  public void shouldNotExecuteConditionBlock() throws URISyntaxException {
    builder.withSource("if false ping ping ping endif").compile().execute();
    assertThat(output.toString(), is(""));
  }

  @Test
  public void shouldExecuteLoopBlock() throws URISyntaxException {
    builder.withSource("do 2 times ping enddo").compile().execute();
    assertThat(output.toString(), is("pingping"));
  }

  @Test
  public void shouldNotExecuteLoopBlock() throws URISyntaxException {
    builder.withSource("do 0 times ping ping ping enddo").compile().execute();
    assertThat(output.toString(), is(""));
  }

  @Test
  public void shouldExitFlow() throws URISyntaxException {
    builder.withSource("ping exit ping").compile().execute();
    assertThat(output.toString(), is("ping"));
  }

  @Test
  public void shouldNotExecuteProcedureDefinition() throws URISyntaxException {
    builder.withSource("procedure my_procedure ping ping end").compile().execute();
    assertThat(output.toString(), is(""));
  }

  @Test
  public void shouldCallProcedure() throws URISyntaxException {
    builder.withSource("call my_procedure exit procedure my_procedure ping end").compile().execute();
    assertThat(output.toString(), is("ping"));
  }
}
