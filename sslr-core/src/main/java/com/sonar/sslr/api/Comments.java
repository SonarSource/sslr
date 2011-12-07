/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ListMultimap;

/**
 * Utility class to manipulate comment tokens
 */
public class Comments implements Iterable<Token> {

  private final ListMultimap<Integer, Token> comments;
  private CommentAnalyser analyser;

  public Comments(ListMultimap<Integer, Token> comments) {
    this.comments = comments;
  }

  public Comments(ListMultimap<Integer, Token> comments, CommentAnalyser analyser) {
    this.comments = comments;
    this.analyser = analyser;
  }

  /**
   * Iterates over the collection of comment tokens
   */
  public Iterator<Token> iterator() {
    return comments.values().iterator();
  }

  /**
   * Get the sorted list of lines containing at least a comment
   */
  public SortedSet<Integer> getLinesOfComment() {
    return new TreeSet<Integer>(comments.keySet());
  }

  /**
   * Return the number of comment tokens
   */
  public int size() {
    return comments.size();
  }

  /**
   * 
   * @param line
   *          the line where we expect to find some comment tokens
   * @return true if there is at least one comment token at provided line
   */
  public boolean hasCommentTokensAtLine(int line) {
    return !getCommentTokensAtLine(line).isEmpty();
  }

  /**
   * 
   * @param line
   *          the line where we expect to find some comment tokens
   * @return the list of comment tokens on this line
   */
  public List<Token> getCommentTokensAtLine(int line) {
    return comments.get(line);
  }

  /**
   * 
   * @param commentValue
   *          the value of the comment
   * @return true if the value is considered to be a blank comment
   */
  public boolean isBlank(String commentValue) {
    if (analyser == null) {
      throw new UnsupportedOperationException(
          "Call to Comments.isBlank(..) methods are not supported as no CommentAnalyser has been defined.");
    }
    return analyser.isBlank(commentValue);
  }

  /**
   * @deprecated see {@link #isBlank(String)}
   */
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

  /**
   * @deprecated see {@link #isBlank(int)}
   */
  @Deprecated
  public boolean isBlankComment(int line) {
    if (hasCommentTokensAtLine(line)) {
      return isBlankComment(getCommentAtLine(line).getValue());
    }
    return false;
  }

  /**
   * @deprecated see {@link #getCommentTokensAtLine(int)}
   */
  @Deprecated
  public Token getCommentAtLine(int line) {
    if (hasCommentTokensAtLine(line)) {
      return getCommentTokensAtLine(line).get(0);
    }
    return null;
  }

  /**
   * 
   * @param line
   *          the line above which some non-blank comment tokens are expected to be found
   * @return true if there is a list one non-blank comment tokens before the provided line
   */
  public boolean isThereCommentBeforeLine(int line) {
    int commentLine = line - 1;
    while (hasCommentTokensAtLine(commentLine)) {
      for (Token comment : getCommentTokensAtLine(commentLine)) {
        if ( !isBlank(comment.getValue())) {
          return true;
        }
      }
      commentLine--;
    }
    return false;
  }

  /**
   * @deprecated see {@link #hasCommentTokensAtLine(int)}
   */
  @Deprecated
  public boolean isThereCommentAtLine(int commentLine) {
    return hasCommentTokensAtLine(commentLine);
  }
}
