/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

public interface GrammarDecorator<GRAMMAR extends Grammar> {

  public void decorate(GRAMMAR grammar);
}
