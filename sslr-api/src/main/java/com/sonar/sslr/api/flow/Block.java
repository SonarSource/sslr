/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Iterator;

import com.sonar.sslr.api.AstNode;

public class Block implements Iterable<AstNode> {

  private final AstNode firstInstruction;
  private AstNode lastInstruction;
  private InstructionIterator iterator;

  public Block(AstNode firstInstruction) {
    this.firstInstruction = firstInstruction;
  }

  public void setLastInstruction(AstNode instruction) {
    this.lastInstruction = instruction;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Block) {
      return firstInstruction == ((Block) object).firstInstruction;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return firstInstruction.hashCode();
  }

  public Iterator<AstNode> iterator() {
    if (lastInstruction == null) {
      throw new IllegalStateException("The last instruction of the block has not been defined.");
    }
    if (iterator == null) {
      iterator = new InstructionIterator();
    }
    iterator.init();
    return iterator;
  }

  private class InstructionIterator implements Iterator<AstNode> {

    private final AstNode parent;
    private int currentIndex;
    private int fromInstructionIndex = -1;
    private int toInstructionIndex = -1;

    private InstructionIterator() {
      parent = firstInstruction.getParent();
      int childIndex = 0;
      for (AstNode child : parent.getChildren()) {
        if (child == firstInstruction) {
          fromInstructionIndex = childIndex;
        } else if (child == lastInstruction) {
          toInstructionIndex = childIndex;
          return;
        }
        childIndex++;
      }
      throw new IllegalStateException("The ");
    }

    public void init() {
      currentIndex = fromInstructionIndex;
    }

    public boolean hasNext() {
      return currentIndex <= toInstructionIndex;
    }

    public AstNode next() {
      return parent.getChild(currentIndex++);
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
