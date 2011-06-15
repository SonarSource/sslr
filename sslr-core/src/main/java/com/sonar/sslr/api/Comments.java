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
  private CommentAnalyser analyser;

  public Comments(ListMultimap<Integer, Token> comments) {
    this.comments = comments;
  }

  public Comments(ListMultimap<Integer, Token> comments, CommentAnalyser analyser) {
    this.comments = comments;
    this.analyser = analyser;
  }

  public Iterator<Token> iterator() {
    return comments.values().iterator();
  }

  public int size() {
    return comments.size();
  }

  public boolean hasCommentTokensAtLine(int line) {
    return !getCommentTokensAtLine(line).isEmpty();
  }

  public List<Token> getCommentTokensAtLine(int line) {
    return comments.get(line);
  }

  public boolean isBlank(int line) {
    return isBlank(getCommentTokensAtLine(line));
  }

  public boolean isBlank(String commentValue) {
    if (analyser == null) {
      throw new UnsupportedOperationException(
          "Call to Comments.isBlank(..) methods are not supported as no CommentAnalyser has been defined.");
    }
    return analyser.isBlank(commentValue);
  }

  private boolean isBlank(List<Token> comments) {
    for (Token comment : comments) {
      if ( !isBlank(comment.getValue()))
        return false;
    }
    return true;
  }

  @Deprecated
  public static boolean isBlankComment(String comment) {
    for (int i = 0; i < comment.length(); i++) {
      char character = comment.charAt(i);
      if ( !Character.isSpaceChar(character) && character != '*' && character != '-' && character != '=') {
        return false;
      }
    }
    return true;
  }

  @Deprecated
  public boolean isBlankComment(int line) {
    if (hasCommentTokensAtLine(line)) {
      return isBlankComment(getCommentAtLine(line).getValue());
    }
    return false;
  }

  @Deprecated
  public Token getCommentAtLine(int line) {
    if (hasCommentTokensAtLine(line)) {
      return getCommentTokensAtLine(line).get(0);
    }
    return null;
  }

  @Deprecated
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

  @Deprecated
  public boolean isThereCommentAtLine(int commentLine) {
    return hasCommentTokensAtLine(commentLine);
  }
}
