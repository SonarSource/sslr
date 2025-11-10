/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.ast;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;

public class SkipFromAstIfOnlyOneChild implements AstNodeSkippingPolicy {

  public static final SkipFromAstIfOnlyOneChild INSTANCE = new SkipFromAstIfOnlyOneChild();

  private SkipFromAstIfOnlyOneChild() {
  }

  @Override
  public boolean hasToBeSkippedFromAst(AstNode node) {
    return node.getNumberOfChildren() == 1;
  }

}
