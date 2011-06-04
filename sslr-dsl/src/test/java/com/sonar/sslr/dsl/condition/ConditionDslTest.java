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

import com.sonar.sslr.dsl.Dsl;

public class ConditionDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  public void shouldParseBasicConditions() throws URISyntaxException {
    Dsl.builder(new ConditionPrinterDsl(), "true").inject(output).compile();
    Dsl.builder(new ConditionPrinterDsl(), "false").inject(output).compile();
    Dsl.builder(new ConditionPrinterDsl(), "false and true").inject(output).compile();
    Dsl.builder(new ConditionPrinterDsl(), "false or true").inject(output).compile();
    Dsl.builder(new ConditionPrinterDsl(), "3 = 2").inject(output).compile();
    Dsl.builder(new ConditionPrinterDsl(), "3 != 2").inject(output).compile();
    Dsl.builder(new ConditionPrinterDsl(), "3 not equals 2").inject(output).compile();
  }

  @Test
  public void shouldComputePrimaryCondition() throws URISyntaxException {
    Dsl.builder(new ConditionPrinterDsl(), "true").inject(output).compile().execute();
    assertThat(output.toString(), is("true"));
  }

  @Test
  public void shouldComputeLogicalAnd() throws URISyntaxException {
    Dsl.builder(new ConditionPrinterDsl(), "true && false").inject(output).compile().execute();
    assertThat(output.toString(), is("false"));

    output = new StringBuilder();
    Dsl.builder(new ConditionPrinterDsl(), "true and true").inject(output).compile().execute();
    assertThat(output.toString(), is("true"));
  }

  @Test
  public void shouldComputeLogicalOr() throws URISyntaxException {
    Dsl.builder(new ConditionPrinterDsl(), "true || false").inject(output).compile().execute();
    assertThat(output.toString(), is("true"));

    output = new StringBuilder();
    Dsl.builder(new ConditionPrinterDsl(), "false or false").inject(output).compile().execute();
    assertThat(output.toString(), is("false"));
  }

  @Test
  public void shouldComputeEqual() throws URISyntaxException {
    Dsl.builder(new ConditionPrinterDsl(), "2 equals 2").inject(output).compile().execute();
    assertThat(output.toString(), is("true"));

    output = new StringBuilder();
    Dsl.builder(new ConditionPrinterDsl(), "3 = 4").inject(output).compile().execute();
    assertThat(output.toString(), is("false"));
  }

  @Test
  public void shouldComputeNotEqual() throws URISyntaxException {
    Dsl.builder(new ConditionPrinterDsl(), "2 != 2").inject(output).compile().execute();
    assertThat(output.toString(), is("false"));

    output = new StringBuilder();
    Dsl.builder(new ConditionPrinterDsl(), "3 not equals 4").inject(output).compile().execute();
    assertThat(output.toString(), is("true"));
  }

}
