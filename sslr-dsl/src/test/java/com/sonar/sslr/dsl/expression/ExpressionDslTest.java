/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.expression;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.DslRunner;

public class ExpressionDslTest {

  StringBuilder output = new StringBuilder();

  @Test
  public void shouldParseBasicExpressions() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4");
    DslRunner.create(new ExpressionPrinterDsl(), "4 * 4");
    DslRunner.create(new ExpressionPrinterDsl(), "4 + 4");
    DslRunner.create(new ExpressionPrinterDsl(), "4 - 4");
    DslRunner.create(new ExpressionPrinterDsl(), "4 / 2");
  }

  @Test
  public void shouldParseCompositeExpressions() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4 / 2 - 2");
    DslRunner.create(new ExpressionPrinterDsl(), "( 3 - 1) * 4 ");
  }

  @Test
  public void shouldParseExpressionsWithDouble() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "2.3 * 2");
  }

  @Test
  public void shouldGetPrimaryValue() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4").inject(output).execute();
    assertThat(output.toString(), is("4.0"));
  }

  @Test
  public void shouldMultiply() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4 * 4").inject(output).execute();
    assertThat(output.toString(), is("16.0"));
  }

  @Test
  public void shouldMultiplyDouble() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4.2 * 2").inject(output).execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldComputeVariables() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "(var1 * var2)").inject(output).putInMemory("var1", 4.2).putInMemory("var2", 2.0).execute();
    assertThat(output.toString(), is("8.4"));
  }

  @Test
  public void shouldAdd() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4 + 4").inject(output).execute();
    assertThat(output.toString(), is("8.0"));
  }

  @Test
  public void shouldSubstract() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4 - 2").inject(output).execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldDivide() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "4 / 2").inject(output).execute();
    assertThat(output.toString(), is("2.0"));
  }

  @Test
  public void shouldComputeCompositeExpressions() throws URISyntaxException {
    DslRunner.create(new ExpressionPrinterDsl(), "(3 - 1) * 4").inject(output).execute();
    assertThat(output.toString(), is("8.0"));
  }
}
