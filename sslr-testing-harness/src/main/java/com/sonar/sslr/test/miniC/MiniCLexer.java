/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.*;

public final class MiniCLexer {

  private MiniCLexer() {
  }

  public static enum Literals implements TokenType {

    INTEGER;

    @Override
    public String getName() {
      return name();
    }

    @Override
    public String getValue() {
      return name();
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

  }

  public static enum Punctuators implements TokenType {

    PAREN_L("("), PAREN_R(")"),
    BRACE_L("{"), BRACE_R("}"),
    EQ("="), COMMA(","), SEMICOLON(";"),
    ADD("+"), SUB("-"), MUL("*"), DIV("/"),
    EQEQ("=="), NE("!="), LT("<"), LTE("<="), GT(">"), GTE(">="),
    INC("++"), DEC("--"),
    HASH("#");

    private final String value;

    private Punctuators(String value) {
      this.value = value;
    }

    @Override
    public String getName() {
      return name();
    }

    @Override
    public String getValue() {
      return value;
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

  }

  public static enum Keywords implements TokenType {

    INT("int"), VOID("void"),
    RETURN("return"), IF("if"), ELSE("else"), WHILE("while"),
    CONTINUE("continue"), BREAK("break");

    private final String value;

    private Keywords(String value) {
      this.value = value;
    }

    @Override
    public String getName() {
      return name();
    }

    @Override
    public String getValue() {
      return value;
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

    public static String[] keywordValues() {
      Keywords[] keywordsEnum = Keywords.values();
      String[] keywords = new String[keywordsEnum.length];
      for (int i = 0; i < keywords.length; i++) {
        keywords[i] = keywordsEnum[i].getValue();
      }
      return keywords;
    }

  }

  public static Lexer create() {
    return Lexer.builder()
        .withFailIfNoChannelToConsumeOneCharacter(true)
        .withChannel(new IdentifierAndKeywordChannel("[a-zA-Z]([a-zA-Z0-9_]*[a-zA-Z0-9])?+", true, Keywords.values()))
        .withChannel(regexp(Literals.INTEGER, "[0-9]+"))
        .withChannel(commentRegexp("(?s)/\\*.*?\\*/"))
        .withChannel(new PunctuatorChannel(Punctuators.values()))
        .withChannel(new BlackHoleChannel("[ \t\r\n]+"))
        .withPreprocessor(new MiniCPreprocessor())
        .build();
  }

}
