/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class ConditionDslTest {

  StringBuilder output = new StringBuilder();
  Dsl.Builder builder = Dsl.builder().setGrammar(new ConditionPrinterDsl()).inject(output);

  @Test
  public void shouldParseBasicConditions() throws URISyntaxException {
    builder.withSource("true").compile();
    builder.withSource("false").compile();
    builder.withSource("false and true").compile();
    builder.withSource("false or true").compile();
    builder.withSource("3 = 2").compile();
    builder.withSource("3 != 2").compile();
    builder.withSource("3 not equals 2").compile();
  }

  @Test
  public void shouldComputePrimaryCondition() throws URISyntaxException {
    builder.withSource("true").compile().execute();
    assertThat(output.toString(), is("true"));
  }

  @Test
  public void shouldComputeLogicalAnd() throws URISyntaxException {
    builder.withSource("true && false").compile().execute();
    assertThat(output.toString(), is("false"));

    resetOutput();
    builder.withSource("true and true").compile().execute();
    assertThat(output.toString(), is("true"));
  }

  @Test
  public void shouldComputeLogicalOr() throws URISyntaxException {
    builder.withSource("true || false").compile().execute();
    assertThat(output.toString(), is("true"));

    resetOutput();
    builder.withSource("false or false").compile().execute();
    assertThat(output.toString(), is("false"));
  }

  @Test
  public void shouldComputeEqual() throws URISyntaxException {
    builder.withSource("2 equals 2").compile().execute();
    assertThat(output.toString(), is("true"));

    resetOutput();
    builder.withSource("3 = 4").compile().execute();
    assertThat(output.toString(), is("false"));
  }

  @Test
  public void shouldComputeNotEqual() throws URISyntaxException {
    builder.withSource("2 != 2").compile().execute();
    assertThat(output.toString(), is("false"));

    resetOutput();
    builder.withSource("3 not equals 4").compile().execute();
    assertThat(output.toString(), is("true"));
  }

  private void resetOutput() {
    output.delete(0, output.length());
  }

}
