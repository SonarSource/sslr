/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.api;

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

  private boolean isThereCommentAtLine(int line) {
    return comments.containsKey(line);
  }

  public Token getCommentAtLine(int line) {
    return comments.get(line);
  }

  public static boolean isBlankComment(String comment) {
    for (int i = 0; i < comment.length(); i++) {
      char character = comment.charAt(i);
      if ( !Character.isSpaceChar(character) && character != '*' && character != '-' && character != '=') {
        return false;
      }
    }
    return true;
  }

  private boolean isBlankComment(int line) {
    if (isThereCommentAtLine(line)) {
      return isBlankComment(getCommentAtLine(line).getValue());
    }
    return false;
  }

  public boolean isThereCommentBeforeLine(int line) {
    int commentLine = line - 1;
    while (isThereCommentAtLine(commentLine)) {
      if ( !isBlankComment(commentLine)) {
        return true;
      }
      commentLine--;
    }
    return false;
  }

  public int size() {
    return comments.size();
  }

}
