/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.sonar.squid.api.CodeCheck;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.SquidAstVisitor;

public abstract class SquidCheck<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> implements CodeCheck {

  public String getKey() {
    return null;
  }

}
