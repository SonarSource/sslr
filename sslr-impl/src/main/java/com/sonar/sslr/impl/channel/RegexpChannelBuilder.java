/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import com.sonar.sslr.api.TokenType;

public final class RegexpChannelBuilder {

  public final static String DIGIT = "[0-9]";
  public final static String OCTAL_DIGIT = "[0-7]";
  public static final String HEXA_DIGIT = "[a-fA-F0-9]";

  public final static RegexpChannel regexp(TokenType type, String... regexpPiece) {
    return new RegexpChannel(type, merge(regexpPiece));
  }

  public final static String opt(String regexpPiece) {
    return regexpPiece + "?";
  }

  public final static String one2n(String regexpPiece) {
    return regexpPiece + "+";
  }

  public final static String o2n(String regexpPiece) {
    return regexpPiece + "*";
  }
  
  public final static String anyButNot(String... character){
    StringBuilder result = new StringBuilder();
    result.append("[^");
    for (int i = 0; i < character.length; i++) {
      result.append(character[i]);
      if (i != character.length - 1) {
      }
    }
    result.append("]");
    return result.toString();
  }

  public final static String g(String... regexpPiece) {
    StringBuilder result = new StringBuilder();
    result.append("(");
    for (int i = 0; i < regexpPiece.length; i++) {
      result.append(regexpPiece[i]);
    }
    result.append(")");
    return result.toString();
  }

  public final static String or(String... regexpPiece) {
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

  private final static String merge(String... piece) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < piece.length; i++) {
      result.append(piece[i]);
    }
    return result.toString();
  }
}
