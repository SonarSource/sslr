/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.sslr.internal.ast.select;

import org.sonar.sslr.ast.AstSelect;

import com.sonar.sslr.api.AstNode;

import javax.annotation.Nullable;

import java.util.List;

public final class AstSelectFactory {

  private static final AstSelect EMPTY = new EmptyAstSelect();

  private AstSelectFactory() {
  }

  public static AstSelect select(@Nullable AstNode node) {
    return node == null ? EMPTY : new SingleAstSelect(node);
  }

  public static AstSelect create(List<AstNode> list) {
    if (list.size() == 1) {
      return new SingleAstSelect(list.get(0));
    } else if (!list.isEmpty()) {
      return new ListAstSelect(list);
    } else {
      return EMPTY;
    }
  }

  public static AstSelect empty() {
    return EMPTY;
  }

}
