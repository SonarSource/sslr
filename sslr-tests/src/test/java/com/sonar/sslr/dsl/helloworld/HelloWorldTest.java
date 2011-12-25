/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;

public class HelloWorldTest {

  StringBuilder output = new StringBuilder();
  Dsl.Builder builder = Dsl.builder().setGrammar(new HelloWorldDsl()).inject(output);

  @Test
  public void shouldGetHelloWorld() throws URISyntaxException {
    builder.withSource("print 'hello world!'").compile().execute();
    assertThat(output.toString(), is("hello world!"));
  }

  @Test
  public void shouldGetHelloFreddy() throws URISyntaxException {
    builder.withSource("print 'hello freddy!'").compile().execute();
    assertThat(output.toString(), is("hello freddy!"));
  }
}
