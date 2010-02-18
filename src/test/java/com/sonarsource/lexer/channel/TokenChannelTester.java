/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer.channel;

import com.google.common.collect.Lists;

import java.util.List;
import java.io.StringReader;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import com.sonarsource.lexer.TokenChannel;
import com.sonarsource.lexer.CodeReader;
import com.sonarsource.lexer.Token;
import com.sonarsource.cobol.parser.CobolKeyword;
import com.sonarsource.cobol.parser.CobolSpecialCharacter;
import com.sonarsource.cobol.parser.CobolTokenType;
import com.sonarsource.cobol.parser.SQLKeyword;
import com.sonarsource.cobol.parser.cobol.*;

public class TokenChannelTester {
  public static void check(String input, TokenChannel channel, Token expectedOutput, List<Token> actual, boolean assertion) {
    actual.clear();
    List<Token> expected = Lists.newArrayList(expectedOutput);

    if (assertion) {
      assertTrue(channel.read(getCodeReader(input)));
      assertThat(actual, is(expected));
    }
    else {
      assertFalse(channel.read(getCodeReader(input)));
    }
  }

  public static void check(String input, TokenChannel channel, List<Token> actual, boolean assertion) {
    check(input, channel, null, actual, assertion);
  }


  private static CodeReader getCodeReader(String s) {
    CodeReader code = new CodeReader(new StringReader(s));
    code.setColumnPosition(7);
    return code;
  }

  public static Token wordToken(String s) {
    Token token = new Token(CobolTokenType.WORD, s);
    return token;
  }


  public static Token cobolKeywordToken(String key) {
    return cobolKeywordToken(key, key);
  }

  public static Token cobolKeywordToken(String key, String value) {
    return new Token(CobolKeyword.valueOf(key.replace('-', '_')), value);
  }

  public static Token cobolSqlKeywordToken(String key, String value) {
    return new Token(SQLKeyword.valueOf(key.replace('-', '_')), value);
  }

  public static Token quotedStringToken(String s) {
    return new Token(CobolTokenType.QUOTEDSTRING, s);
  }

  public static Token hexadecimalToken(String s) {
    return new Token(CobolTokenType.HEXADECIMAL, s);
  }

  public static Token integerToken(String s) {
    return new Token(CobolTokenType.CBL_INTEGER, s);
  }

  public static Token specialCharToken(String s) {
    for (CobolSpecialCharacter gtt : CobolSpecialCharacter.values()) {
      if (gtt.getValue().equals(s)) {
        return new Token(gtt, gtt.getValue());
      }
    }
    return null;
  }


}
