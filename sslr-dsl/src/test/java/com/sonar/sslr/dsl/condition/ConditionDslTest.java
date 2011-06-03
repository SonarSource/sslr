/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.DslRunner;

public class ConditionDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  public void shouldParseBasicConditions() throws URISyntaxException {
    DslRunner.builder(new ConditionPrinterDsl(), "true").inject(output).build();
    DslRunner.builder(new ConditionPrinterDsl(), "false").inject(output).build();
    DslRunner.builder(new ConditionPrinterDsl(), "false and true").inject(output).build();
    DslRunner.builder(new ConditionPrinterDsl(), "false or true").inject(output).build();
    DslRunner.builder(new ConditionPrinterDsl(), "3 = 2").inject(output).build();
    DslRunner.builder(new ConditionPrinterDsl(), "3 != 2").inject(output).build();
    DslRunner.builder(new ConditionPrinterDsl(), "3 not equals 2").inject(output).build();
  }

  @Test
  public void shouldComputePrimaryCondition() throws URISyntaxException {
    DslRunner.builder(new ConditionPrinterDsl(), "true").inject(output).build().execute();
    assertThat(output.toString(), is("true"));
  }

  @Test
  public void shouldComputeLogicalAnd() throws URISyntaxException {
    DslRunner.builder(new ConditionPrinterDsl(), "true && false").inject(output).build().execute();
    assertThat(output.toString(), is("false"));

    output = new StringBuilder();
    DslRunner.builder(new ConditionPrinterDsl(), "true and true").inject(output).build().execute();
    assertThat(output.toString(), is("true"));
  }

  @Test
  public void shouldComputeLogicalOr() throws URISyntaxException {
    DslRunner.builder(new ConditionPrinterDsl(), "true || false").inject(output).build().execute();
    assertThat(output.toString(), is("true"));

    output = new StringBuilder();
    DslRunner.builder(new ConditionPrinterDsl(), "false or false").inject(output).build().execute();
    assertThat(output.toString(), is("false"));
  }

  @Test
  public void shouldComputeEqual() throws URISyntaxException {
    DslRunner.builder(new ConditionPrinterDsl(), "2 equals 2").inject(output).build().execute();
    assertThat(output.toString(), is("true"));

    output = new StringBuilder();
    DslRunner.builder(new ConditionPrinterDsl(), "3 = 4").inject(output).build().execute();
    assertThat(output.toString(), is("false"));
  }

  @Test
  public void shouldComputeNotEqual() throws URISyntaxException {
    DslRunner.builder(new ConditionPrinterDsl(), "2 != 2").inject(output).build().execute();
    assertThat(output.toString(), is("false"));

    output = new StringBuilder();
    DslRunner.builder(new ConditionPrinterDsl(), "3 not equals 4").inject(output).build().execute();
    assertThat(output.toString(), is("true"));
  }

}
