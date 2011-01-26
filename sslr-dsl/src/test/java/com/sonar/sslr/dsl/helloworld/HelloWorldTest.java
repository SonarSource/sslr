/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;
import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.DslRunner;

public class HelloWorldTest {

  @Test
  public void shouldGetHelloWorld() throws URISyntaxException {
    StringWriter output = new StringWriter();
    DslRunner.execute(new HelloWorldDsl(output), "hello world!");
    
    //assertThat(output.toString(), is("hello world!"));
  }
}
