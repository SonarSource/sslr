/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.lexer.LexerMatchers.*;
import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.junit.Assert.*;
import static org.sonar.test.channel.ChannelMatchers.*;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;

public class PunctuatorChannelTest {

  private final PunctuatorChannel channel = new PunctuatorChannel(MyPunctuatorAndOperator.values());
  private final Lexer lexer = mockLexer();

  @Test
  public void testConsumeSpecialCharacters() {
    assertThat(channel, consume("**=", lexer));
    assertThat(lexer.getTokens(), hasToken("*", MyPunctuatorAndOperator.STAR));

    assertThat(channel, consume(",=", lexer));
    assertThat(lexer.getTokens(), hasToken(",", MyPunctuatorAndOperator.COLON));

    assertThat(channel, consume("=*", lexer));
    assertThat(lexer.getTokens(), hasToken("=", MyPunctuatorAndOperator.EQUAL));

    assertThat(channel, consume("==,", lexer));
    assertThat(lexer.getTokens(), hasToken("==", MyPunctuatorAndOperator.EQUAL_OP));

    assertThat(channel, consume("*=,", lexer));
    assertThat(lexer.getTokens(), hasToken("*=", MyPunctuatorAndOperator.MUL_ASSIGN));

    assertFalse(channel.consume(new CodeReader("!"), lexer));
  }

  @Test
  public void testNotConsumeWord() {
    assertFalse(channel.consume(new CodeReader("word"), lexer));
  }

  private enum MyPunctuatorAndOperator implements TokenType {
    STAR("*"), COLON(","), EQUAL("="), EQUAL_OP("=="), MUL_ASSIGN("*="), NOT_EQUAL("!=");

    private final String value;

    private MyPunctuatorAndOperator(String value) {
      this.value = value;
    }

    public String getName() {
      return name();
    }

    public String getValue() {
      return value;
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

  }
}
