/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.DslMemory;
import com.sonar.sslr.dsl.DslRunner;

public class CalculatorDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  public void shouldParseBasicExpressions() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4").inject(output).build();
    DslRunner.builder(new CalculatorPrinterDsl(), "4 * 4").inject(output).build();
    DslRunner.builder(new CalculatorPrinterDsl(), "4 + 4").inject(output).build();
    DslRunner.builder(new CalculatorPrinterDsl(), "4 - 4").inject(output).build();
    DslRunner.builder(new CalculatorPrinterDsl(), "4 / 2").inject(output).build();
  }

  @Test
  public void shouldParseCompositeExpressions() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4 / 2 - 2").inject(output).build();
    DslRunner.builder(new CalculatorPrinterDsl(), "( 3 - 1) * 4 ").inject(output).build();
  }

  @Test
  public void shouldParseExpressionsWithDouble() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "2.3 * 2").inject(output).build();
  }

  @Test
  public void shouldGetPrimaryValue() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4").inject(output).build().execute();
    assertThat(output.toString(), is("4.0"));
  }

  @Test
  public void shouldMultiply() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4 * 4").inject(output).build().execute();
    assertThat(output.toString(), is("16.0"));
  }

  @Test
  public void shouldMultiplyDouble() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4.2 * 2").inject(output).build().execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldComputeVariables() throws URISyntaxException {
    DslMemory memory = new DslMemory();
    memory.put("var1", 4.2);
    memory.put("var2", 2.0);
    DslRunner.builder(new CalculatorPrinterDsl(), "(var1 * var2)").inject(output).inject(memory).build().execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldAdd() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4 + 4").inject(output).build().execute();
    assertThat(output.toString(), is("8.0"));
  }

  @Test
  public void shouldSubstract() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4 - 2").inject(output).build().execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldDivide() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "4 / 2").inject(output).build().execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldComputeCompositeExpressions() throws URISyntaxException {
    DslRunner.builder(new CalculatorPrinterDsl(), "(3 - 1) * 4").inject(output).build().execute();
    assertThat(output.toString(), is("8.0"));
  }
}
