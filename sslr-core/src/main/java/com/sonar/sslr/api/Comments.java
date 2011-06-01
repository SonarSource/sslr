/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Comments implements Iterable<Token> {

  private Map<Integer, Token> comments = new HashMap<Integer, Token>();

  public Comments(Map<Integer, Token> comments) {
    this.comments = comments;
  }

  public Iterator<Token> iterator() {
    return comments.values().iterator();
  }

  public Token getCommentAtLine(int line) {
    return comments.get(line);
  }

  public int size() {
    return comments.size();
  }

}
