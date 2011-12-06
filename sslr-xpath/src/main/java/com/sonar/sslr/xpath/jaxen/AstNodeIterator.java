/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.jaxen;

import java.util.Iterator;
import java.util.List;

import com.sonar.sslr.api.AstNode;

public class AstNodeIterator implements Iterator {

  private List<AstNode> nodes;
  private String childNameFilter;
  private int index = 0;

  public AstNodeIterator(AstNode node, String childNameFilter) {
    this.nodes = node.getChildren();
    this.childNameFilter = childNameFilter;
  }

  public boolean hasNext() {
    while (index < nodes.size()) {
      if (nodes.get(index).getName().equals(childNameFilter)) {
        return true;
      }
      index++;
    }
    return false;
  }

  public Object next() {
    while (index < nodes.size()) {
      if (nodes.get(index).getName().equals(childNameFilter)) {
        AstNode next = nodes.get(index);
        index++;
        return next;
      }
      index++;
    }
    return null;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
