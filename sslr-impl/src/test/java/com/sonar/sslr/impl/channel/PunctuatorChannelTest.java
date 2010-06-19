/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.sonar.test.Matchers.consume;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.TokenType;

public class PunctuatorChannelTest {

  private PunctuatorChannel channel = new PunctuatorChannel(MyPunctuatorAndOperator.values());
  private LexerOutput output = new LexerOutput();

  @Test
  public void testConsumeSpecialCharacters() {
    assertThat(channel, consume("**=", output));
    assertThat(output, hasToken("*", MyPunctuatorAndOperator.STAR));

    assertThat(channel, consume(",=", output));
    assertThat(output, hasToken(",", MyPunctuatorAndOperator.COLON));

    assertThat(channel, consume("=*", output));
    assertThat(output, hasToken("=", MyPunctuatorAndOperator.EQUAL));

    assertThat(channel, consume("==,", output));
    assertThat(output, hasToken("==", MyPunctuatorAndOperator.EQUAL_OP));

    assertThat(channel, consume("*=,", output));
    assertThat(output, hasToken("*=", MyPunctuatorAndOperator.MUL_ASSIGN));
  }

  @Test
  public void testNotConsumeWord() {
    assertFalse(channel.consume(new CodeReader("word"), output));
  }

  private enum MyPunctuatorAndOperator implements TokenType {
    STAR("*"), COLON(","), EQUAL("="), EQUAL_OP("=="), MUL_ASSIGN("*=");

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
