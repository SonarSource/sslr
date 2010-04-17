/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.PunctuatorTokenType;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.LexerOutput;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PunctuatorChannelTest {

  private PunctuatorChannel channel = new PunctuatorChannel(MyCharacters.values());
  private LexerOutput output = new LexerOutput();

  @Test
  public void testConsumeSpecialCharacters() {
    assertTrue(channel.consum(new CodeReader("*"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyCharacters.STAR));

    assertTrue(channel.consum(new CodeReader(","), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyCharacters.COLON));
  }

  @Test
  public void testNotConsumeWord() {
    assertFalse(channel.consum(new CodeReader("word"), output));
  }

  private enum MyCharacters implements PunctuatorTokenType {
    STAR("*"), COLON(",");

    private final String value;

    private MyCharacters(String value) {
      this.value = value;
    }

    public char getChar() {
      return value.charAt(0);
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
