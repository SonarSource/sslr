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
  
  public int size() {
    return comments.size();
  }
  
  public boolean hasCommentTokensAtLine(int line) {
  	return !getCommentTokensAtLine(line).isEmpty();
  }

  public List<Token> getCommentTokensAtLine(int line) {
    return comments.get(line);
  }
  
  public static boolean isBlank(String commentLine) {
    for (int i = 0; i < commentLine.length(); i++) {
      if (Character.isLetterOrDigit(commentLine.charAt(i)))
        return false;
    }

    return true;
  }
  
  public boolean isBlank(int line) {
  	return isBlank(getCommentTokensAtLine(line));
  }
  
  public static boolean isBlank(List<Token> comments) {
  	for (Token comment: comments) {
  		if (!isBlank(comment.getValue())) return false;
  	}
  	
  	return true;
  }
  
  /* The logic in the method is flawed, does not handle multi-line comments, wrong method name */
  @Deprecated
  public boolean isThereCommentBeforeLine(int line) {
  	int commentLine = line - 1;
    while (hasCommentTokensAtLine(commentLine)) {
      if (!isBlank(commentLine)) {
        return true;
      }
      commentLine--;
    }
    return false;
  }

}
