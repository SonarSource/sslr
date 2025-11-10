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
package org.sonar.sslr.internal.matchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Node of a parse tree.
 */
public class ParseNode {

  private final int startIndex;
  private final int endIndex;
  private final List<ParseNode> children;
  private final Matcher matcher;

  public ParseNode(int startIndex, int endIndex, List<ParseNode> children, Matcher matcher) {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.children = new ArrayList<>(children);
    this.matcher = matcher;
  }

  /**
   * Leaf node.
   */
  public ParseNode(int startIndex, int endIndex, Matcher matcher) {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.matcher = matcher;
    this.children = Collections.emptyList();
  }

  public int getStartIndex() {
    return startIndex;
  }

  /**
   * Be aware that element of input with this index is not included into this node.
   */
  public int getEndIndex() {
    return endIndex;
  }

  public List<ParseNode> getChildren() {
    return children;
  }

  public Matcher getMatcher() {
    return matcher;
  }

}
