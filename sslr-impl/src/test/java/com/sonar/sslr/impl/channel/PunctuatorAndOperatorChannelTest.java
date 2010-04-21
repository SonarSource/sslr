/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.LexerOutput;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PunctuatorAndOperatorChannelTest {

  private PunctuatorAndOperatorChannel channel = new PunctuatorAndOperatorChannel(MyPunctuatorAndOperator.values());
  private LexerOutput output = new LexerOutput();

  @Test
  public void testConsumeSpecialCharacters() {
    assertTrue(channel.consum(new CodeReader("**="), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyPunctuatorAndOperator.STAR));

    assertTrue(channel.consum(new CodeReader(",="), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyPunctuatorAndOperator.COLON));

    assertTrue(channel.consum(new CodeReader("=*"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyPunctuatorAndOperator.EQUAL));

    assertTrue(channel.consum(new CodeReader("==,"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyPunctuatorAndOperator.EQUAL_OP));

    assertTrue(channel.consum(new CodeReader("*=,"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyPunctuatorAndOperator.MUL_ASSIGN));
  }

  @Test
  public void testNotConsumeWord() {
    assertFalse(channel.consum(new CodeReader("word"), output));
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

    public boolean hasToBeSkippedFromAst() {
      return false;
    }

  }
}
