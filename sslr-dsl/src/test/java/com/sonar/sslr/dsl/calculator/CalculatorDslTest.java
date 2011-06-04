/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class CalculatorDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  public void shouldParseBasicExpressions() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4").inject(output).compile();
    Dsl.builder(new CalculatorPrinterDsl(), "4 * 4").inject(output).compile();
    Dsl.builder(new CalculatorPrinterDsl(), "4 + 4").inject(output).compile();
    Dsl.builder(new CalculatorPrinterDsl(), "4 - 4").inject(output).compile();
    Dsl.builder(new CalculatorPrinterDsl(), "4 / 2").inject(output).compile();
  }

  @Test
  public void shouldParseCompositeExpressions() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4 / 2 - 2").inject(output).compile();
    Dsl.builder(new CalculatorPrinterDsl(), "( 3 - 1) * 4 ").inject(output).compile();
  }

  @Test
  public void shouldParseExpressionsWithDouble() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "2.3 * 2").inject(output).compile();
  }

  @Test
  public void shouldGetPrimaryValue() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4").inject(output).compile().execute();
    assertThat(output.toString(), is("4.0"));
  }

  @Test
  public void shouldMultiply() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4 * 4").inject(output).compile().execute();
    assertThat(output.toString(), is("16.0"));
  }

  @Test
  public void shouldMultiplyDouble() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4.2 * 2").inject(output).compile().execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldComputeVariables() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "(var1 * var2)").inject(output).put("var1", 4.2).put("var2", 2.0).compile().execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldAdd() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4 + 4").inject(output).compile().execute();
    assertThat(output.toString(), is("8.0"));
  }

  @Test
  public void shouldSubstract() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4 - 2").inject(output).compile().execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldDivide() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "4 / 2").inject(output).compile().execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldComputeCompositeExpressions() throws URISyntaxException {
    Dsl.builder(new CalculatorPrinterDsl(), "(3 - 1) * 4").inject(output).compile().execute();
    assertThat(output.toString(), is("8.0"));
  }
}
