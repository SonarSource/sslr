/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

public abstract class AstListener<OUTPUT extends AstListenersOutput> {

  public abstract void startListening(AstNode astNode, OUTPUT output);

  public void stopListening(AstNode astNode, OUTPUT output) {
  }
}
