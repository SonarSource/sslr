/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Comments implements Iterable<Token> {

  private Map<Integer, Token> comments = new HashMap<Integer, Token>();

  public Comments(List<Token> comments) {
    for (Token comment : comments) {
      this.comments.put(comment.getLine(), comment);
    }
  }

  public Iterator<Token> iterator() {
    return comments.values().iterator();
  }

  public boolean isThereCommentAtLine(int line) {
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

  public boolean isBlankComment(int line) {
    if (isThereCommentAtLine(line)) {
      return isBlankComment(getCommentAtLine(line).getValue());
    }
    return false;
  }

}
