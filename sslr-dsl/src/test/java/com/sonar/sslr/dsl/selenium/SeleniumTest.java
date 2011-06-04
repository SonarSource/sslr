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

  @Test
  public void shouldParseAllStatements() throws URISyntaxException {
    Dsl.builder(new SeleniumDsl(), "open 'http://www.google.com'").compile();
    Dsl.builder(new SeleniumDsl(), "assert that title is 'Google'").compile();
    Dsl.builder(new SeleniumDsl(), "assert that size <= 2 Ko").compile();
  }
}
