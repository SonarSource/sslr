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
  Dsl.Builder builder = Dsl.builder().setGrammar(new CalculatorPrinterDsl()).inject(output);

  @Test
  public void shouldParseBasicExpressions() throws URISyntaxException {
    builder.withSource("4").compile();
    builder.withSource("4 * 4").compile();
    builder.withSource("4 + 4").compile();
    builder.withSource("4 - 4").compile();
    builder.withSource("4 / 2").compile();
  }

  @Test
  public void shouldParseCompositeExpressions() throws URISyntaxException {
    builder.withSource("4 / 2 - 2").compile();
    builder.withSource("( 3 - 1) * 4 ").compile();
  }

  @Test
  public void shouldParseExpressionsWithDouble() throws URISyntaxException {
    builder.withSource("2.3 * 2").compile();
  }

  @Test
  public void shouldGetPrimaryValue() throws URISyntaxException {
    builder.withSource("4").compile().execute();
    assertThat(output.toString(), is("4.0"));
  }

  @Test
  public void shouldMultiply() throws URISyntaxException {
    builder.withSource("4 * 4").compile().execute();
    assertThat(output.toString(), is("16.0"));
  }

  @Test
  public void shouldMultiplyDouble() throws URISyntaxException {
    builder.withSource("4.2 * 2").compile().execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldComputeVariables() throws URISyntaxException {
    builder.withSource("(var1 * var2)").put("var1", 4.2).put("var2", 2.0).compile().execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldAdd() throws URISyntaxException {
    builder.withSource("4 + 4").compile().execute();
    assertThat(output.toString(), is("8.0"));
  }

  @Test
  public void shouldSubstract() throws URISyntaxException {
    builder.withSource("4 - 2").compile().execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldDivide() throws URISyntaxException {
    builder.withSource("4 / 2").compile().execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldComputeCompositeExpressions() throws URISyntaxException {
    builder.withSource("(3 - 1) * 4").compile().execute();
    assertThat(output.toString(), is("8.0"));
  }
}
