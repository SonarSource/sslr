/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.Scope;
import com.sonar.sslr.api.symboltable.Type;

public class BuiltInType implements Type {

  private final String name;

  public BuiltInType(String name) {
    this.name = name;
  }

  public AstNode getAstNode() {
    return null;
  }

  public Scope getEnclosingScope() {
    throw new UnsupportedOperationException();
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "BuiltInType{" + name + "}";
  }

}
