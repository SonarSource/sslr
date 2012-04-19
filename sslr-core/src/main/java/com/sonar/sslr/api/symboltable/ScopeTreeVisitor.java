/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

public interface ScopeTreeVisitor {

  void visitScope(Scope scope);

  void leaveScope(Scope scope);

}
