/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.channel;

import com.sonar.sslr.api.TokenType;

public final class RegexpChannelBuilder {

  public static final String DIGIT = "\\d";
  public static final String ANY_CHAR = "[\\s\\S]";
  public static final String OCTAL_DIGIT = "[0-7]";
  public static final String HEXA_DIGIT = "[a-fA-F0-9]";

  private RegexpChannelBuilder() {
  }

  public static RegexpChannel regexp(TokenType type, String... regexpPiece) {
    return new RegexpChannel(type, merge(regexpPiece));
  }

  public static CommentRegexpChannel commentRegexp(String... regexpPiece) {
    return new CommentRegexpChannel(merge(regexpPiece));
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
