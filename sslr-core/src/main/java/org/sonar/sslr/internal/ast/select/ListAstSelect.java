/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.ast.select;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import java.util.Iterator;
import java.util.List;

/**
 * {@link AstSelect} which contains more than one element.
 */
public class ListAstSelect implements AstSelect {

  private final List<AstNode> list;

  public ListAstSelect(List<AstNode> list) {
    this.list = list;
  }

  public AstSelect children() {
    List<AstNode> result = Lists.newArrayList();
    for (AstNode node : list) {
      result.addAll(node.getChildren());
    }
    return AstSelectFactory.create(result);
  }

  public AstSelect children(AstNodeType type) {
    List<AstNode> result = Lists.newArrayList();
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

  public AstSelect children(AstNodeType... types) {
    List<AstNode> result = Lists.newArrayList();
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

  public AstSelect nextSibling() {
    List<AstNode> result = Lists.newArrayList();
    for (AstNode node : list) {
      node = node.getNextSibling();
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  public AstSelect previousSibling() {
    List<AstNode> result = Lists.newArrayList();
    for (AstNode node : list) {
      node = node.getPreviousSibling();
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  public AstSelect parent() {
    List<AstNode> result = Lists.newArrayList();
    for (AstNode node : list) {
      node = node.getParent();
      if (node != null) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  public AstSelect firstAncestor(AstNodeType type) {
    List<AstNode> result = Lists.newArrayList();
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

  public AstSelect descendants(AstNodeType type) {
    List<AstNode> result = Lists.newArrayList();
    for (AstNode node : list) {
      result.addAll(node.getDescendants(type));
    }
    return AstSelectFactory.create(result);
  }

  public AstSelect descendants(AstNodeType... types) {
    List<AstNode> result = Lists.newArrayList();
    for (AstNode node : list) {
      result.addAll(node.getDescendants(types));
    }
    return AstSelectFactory.create(result);
  }

  public boolean isEmpty() {
    return false;
  }

  public boolean isNotEmpty() {
    return true;
  }

  public AstSelect filter(Predicate<AstNode> predicate) {
    List<AstNode> result = Lists.newArrayList();
    for (AstNode node : list) {
      if (predicate.apply(node)) {
        result.add(node);
      }
    }
    return AstSelectFactory.create(result);
  }

  public int size() {
    return list.size();
  }

  public AstNode get(int index) {
    return list.get(index);
  }

  public Iterator<AstNode> iterator() {
    return list.iterator();
  }

}
