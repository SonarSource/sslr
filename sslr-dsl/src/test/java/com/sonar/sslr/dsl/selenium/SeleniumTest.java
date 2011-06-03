/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium;

import java.net.URISyntaxException;

import org.junit.Test;

import com.sonar.sslr.dsl.DslRunner;

public class SeleniumTest {

  Selenium selenium = new Selenium();

  @Test
  public void shouldParseAllStatements() throws URISyntaxException {
    DslRunner.builder(new SeleniumDsl(), "open 'http://www.google.com'").build();
    DslRunner.builder(new SeleniumDsl(), "assert that title is 'Google'").build();
    DslRunner.builder(new SeleniumDsl(), "assert that size <= 2 Ko").build();
  }
}
