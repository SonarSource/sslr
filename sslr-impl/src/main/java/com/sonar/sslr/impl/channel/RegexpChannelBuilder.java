/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import com.sonar.sslr.api.TokenType;

public final class RegexpChannelBuilder {

  public final static String D = "[0-9]";

  public final static RegexpChannel regexp(TokenType type, String... regexpPiece) {
    StringBuilder regexp = new StringBuilder();
    for (int i = 0; i < regexpPiece.length; i++) {
      regexp.append(regexpPiece[i]);
    }
    return new RegexpChannel(type, regexp.toString());
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
}
