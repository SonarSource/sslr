/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import com.sonar.sslr.api.TokenType;

public final class RegexpChannelBuilder {

  public final static String DIGIT = "\\d";
  public final static String ANY_CHAR = "[\\s\\S]";
  public final static String OCTAL_DIGIT = "[0-7]";
  public static final String HEXA_DIGIT = "[a-fA-F0-9]";

  private RegexpChannelBuilder() {
  }

  public static RegexpChannel regexp(TokenType type, String... regexpPiece) {
    return new RegexpChannel(type, merge(regexpPiece));
  }

  public static CommentRegexpChannel commentRegexp(String... regexpPiece) {
    return new CommentRegexpChannel(merge(regexpPiece));
  }

  public static CommentRegexpChannel commentRegexp(int removeBefore, int removeAfter, String... regexpPiece) {
    return new CommentRegexpChannel(merge(regexpPiece), removeBefore, removeAfter);
  }

  public static CommentRegexpChannel commentRegexp(boolean trimBeforeRemove, String... regexpPiece) {
    return new CommentRegexpChannel(merge(regexpPiece), trimBeforeRemove);
  }

  public static CommentRegexpChannel commentRegexp(int removeBefore, int removeAfter, boolean trimBeforeRemove, String... regexpPiece) {
    return new CommentRegexpChannel(merge(regexpPiece), removeBefore, removeAfter, trimBeforeRemove);
  }

  public static String opt(String regexpPiece) {
    return regexpPiece + "?+";
  }

  public static String and(String... regexpPieces) {
    StringBuilder result = new StringBuilder();
    for (String rexpPiece : regexpPieces) {
      result.append(rexpPiece);
    }
    return result.toString();
  }

  public static String one2n(String regexpPiece) {
    return regexpPiece + "++";
  }

  public static String o2n(String regexpPiece) {
    return regexpPiece + "*+";
  }

  public static String anyButNot(String... character) {
    StringBuilder result = new StringBuilder();
    result.append("[^");
    for (int i = 0; i < character.length; i++) {
      result.append(character[i]);
      if (i != character.length - 1) { // NOSONAR
        // do nothing
      }
    }
    result.append("]");
    return result.toString();
  }

  public static String g(String... regexpPiece) {
    StringBuilder result = new StringBuilder();
    result.append("(");
    for (String element : regexpPiece) {
      result.append(element);
    }
    result.append(")");
    return result.toString();
  }

  public static String or(String... regexpPiece) {
    StringBuilder result = new StringBuilder();
    result.append("(");
    for (int i = 0; i < regexpPiece.length; i++) {
      result.append(regexpPiece[i]);
      if (i != regexpPiece.length - 1) {
        result.append("|");
      }
    }
    result.append(")");
    return result.toString();
  }

  private static String merge(String... piece) {
    StringBuilder result = new StringBuilder();
    for (String element : piece) {
      result.append(element);
    }
    return result.toString();
  }
}
