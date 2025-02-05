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
package org.sonar.sslr.internal.vm;

import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.matchers.ParseNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineStack {

  private final MachineStack parent;
  private MachineStack child;

  private final List<ParseNode> subNodes;
  private int address;
  private int index;
  private boolean ignoreErrors;
  private Matcher matcher;

  private int leftRecursion;
  private int calledAddress;

  public MachineStack() {
    this.parent = null;
    this.subNodes = Collections.emptyList();
    this.index = -1;
  }

  private MachineStack(MachineStack parent) {
    this.parent = parent;
    this.subNodes = new ArrayList<>();
  }

  public MachineStack parent() {
    return parent;
  }

  public MachineStack getOrCreateChild() {
    if (child == null) {
      child = new MachineStack(this);
    }
    return child;
  }

  public boolean isReturn() {
    return matcher != null;
  }

  /**
   * @return true, if this object denotes an empty stack
   */
  public boolean isEmpty() {
    return index == -1;
  }

  public int address() {
    return address;
  }

  public void setAddress(int address) {
    this.address = address;
  }

  public int index() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public boolean isIgnoreErrors() {
    return ignoreErrors;
  }

  public void setIgnoreErrors(boolean ignoreErrors) {
    this.ignoreErrors = ignoreErrors;
  }

  public Matcher matcher() {
    return matcher;
  }

  public void setMatcher(@Nullable Matcher matcher) {
    this.matcher = matcher;
  }

  public int leftRecursion() {
    return leftRecursion;
  }

  public void setLeftRecursion(int leftRecursion) {
    this.leftRecursion = leftRecursion;
  }

  public int calledAddress() {
    return calledAddress;
  }

  public void setCalledAddress(int calledAddress) {
    this.calledAddress = calledAddress;
  }

  public List<ParseNode> subNodes() {
    return subNodes;
  }

}
