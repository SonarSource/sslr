/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import com.sonar.sslr.api.AstNode;

public abstract class StructuralSearchMatcher {

  public abstract AstNode match(AstNode node);
}
