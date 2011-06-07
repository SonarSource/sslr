/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ListMultimap;

public class Comments implements Iterable<Token> {

  private ListMultimap<Integer, Token> comments;

  public Comments(ListMultimap<Integer, Token> comments) {
    this.comments = comments;
  }

  public Iterator<Token> iterator() {
    return comments.values().iterator();
  }

  public List<Token> getCommentAtLine(int line) {
    return comments.get(line);
  }

  public int size() {
    return comments.size();
  }

}
