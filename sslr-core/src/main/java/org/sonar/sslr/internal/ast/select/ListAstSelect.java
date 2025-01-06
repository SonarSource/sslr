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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.sslr.ast.AstSelect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * {@link AstSelect} which contains more than one element.
 */
public class ListAstSelect implements AstSelect {

  private final List<AstNode> list;

  public ListAstSelect(List<AstNode> list) {
    this.list = list;
  }

  @Override
  public AstSelect children() {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      result.addAll(node.getChildren());
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect children(AstNodeType type) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      // Don't use "getChildren(type)", because under the hood it will create an array of types and new List to keep the result
      for (AstNode child : node.getChildren()) {
        // Don't use "is(type)", because under the hood it will create an array of types
        if (child.getType() == type) {
          result.add(child);
        }
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect children(AstNodeType... types) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      // Don't use "getChildren(type)", because it will create new List to keep the result
      for (AstNode child : node.getChildren()) {
        if (child.is(types)) {
          result.add(child);
        }
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect nextSibling() {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      node = node.getNextSibling();
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect previousSibling() {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      node = node.getPreviousSibling();
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect parent() {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      node = node.getParent();
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect firstAncestor(AstNodeType type) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      node = node.getParent();
      while (node != null && node.getType() != type) {
        node = node.getParent();
      }
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect firstAncestor(AstNodeType... types) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      node = node.getParent();
      while (node != null && !node.is(types)) {
        node = node.getParent();
      }
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect descendants(AstNodeType type) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      result.addAll(node.getDescendants(type));
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect descendants(AstNodeType... types) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      result.addAll(node.getDescendants(types));
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean isNotEmpty() {
    return true;
  }

  @Override
  public AstSelect filter(AstNodeType type) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      if (node.getType() == type) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect filter(AstNodeType... types) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      if (node.is(types)) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public AstSelect filter(Predicate<AstNode> predicate) {
    List<AstNode> result = new ArrayList<>();
    for (AstNode node : list) {
      if (predicate.test(node)) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public AstNode get(int index) {
    return list.get(index);
  }

  @Override
  public Iterator<AstNode> iterator() {
    return list.iterator();
  }

}
