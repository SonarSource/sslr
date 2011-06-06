/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.Dsl;

public class SeleniumTest {

  Selenium selenium = new Selenium();
  Dsl.Builder builder = Dsl.builder().setGrammar(new SeleniumDsl());

  @Test
  public void shouldParseAllStatements() throws URISyntaxException {
    builder.withSource("open 'http://www.google.com'").compile();
    builder.withSource("assert that title is 'Google'").compile();
    builder.withSource("assert that size <= 2 Ko").compile();
  }
}
