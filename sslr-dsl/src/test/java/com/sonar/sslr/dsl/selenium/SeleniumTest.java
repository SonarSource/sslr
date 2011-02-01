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
    DslRunner.create(new SeleniumDsl(), "open 'http://www.google.com'");
    DslRunner.create(new SeleniumDsl(), "assert that title is 'Google'");
    DslRunner.create(new SeleniumDsl(), "assert that size <= 2 Ko");
  }
}
