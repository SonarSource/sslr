/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.impl.typed;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;

import javax.annotation.Nullable;

import java.lang.reflect.Field;

public class AstNodeReflector {

  private static final Field TOKEN_FIELD = getAstNodeField("token");
  private static final Field CHILD_INDEX_FIELD = getAstNodeField("childIndex");
  private static final Field PARENT_FIELD = getAstNodeField("parent");

  private AstNodeReflector() {
  }

  public static void setToken(AstNode astNode, @Nullable Token token) {
    ReflectionUtils.setField(TOKEN_FIELD, astNode, token);
  }

  public static void setChildIndex(AstNode astNode, int childIndex) {
    ReflectionUtils.setField(CHILD_INDEX_FIELD, astNode, childIndex);
  }

  public static void setParent(AstNode astNode, @Nullable AstNode parent) {
    ReflectionUtils.setField(PARENT_FIELD, astNode, parent);
  }

  private static Field getAstNodeField(String name) {
    return ReflectionUtils.getField(AstNode.class, name);
  }

}
