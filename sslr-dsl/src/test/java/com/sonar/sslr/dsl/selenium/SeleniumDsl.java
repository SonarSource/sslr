/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.selenium;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.CommandListDsl;
import static com.sonar.sslr.dsl.DslTokenType.*;

public class SeleniumDsl extends CommandListDsl {

  public Rule openUrl;
  public Rule assertTitle;
  public Rule assertSize;

  public SeleniumDsl() {
    command.isOr(openUrl, assertTitle, assertSize);

    openUrl.is("open", LITERAL);
    assertTitle.is("assert", "that", "title", "is", LITERAL);
    assertSize.is("assert", "that", "size", "<", "=", INTEGER, "Ko");

  }
}
