/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.BasicDsl;

public class SeleniumDsl extends BasicDsl {

  public Rule openUrl;
  public Rule assertTitle;
  public Rule assertSize;

  public SeleniumDsl() {
    statement.isOr(openUrl, assertTitle, assertSize);

    openUrl.is("open", literal);
    assertTitle.is("assert", "that", "title", "is", literal);
    assertSize.is("assert", "that", "size", "<", "=", integer, "Ko");

  }
}
