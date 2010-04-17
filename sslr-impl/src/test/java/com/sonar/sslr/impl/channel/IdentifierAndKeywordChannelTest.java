/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.LexerOutput;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class IdentifierAndKeywordChannelTest {

  private IdentifierAndKeywordChannel channel = new IdentifierAndKeywordChannel(MyKeywords.values());
  private LexerOutput output = new LexerOutput();

  @Test
  public void testConsumWord() {
    assertTrue(channel.consum(new CodeReader("word"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) GenericTokenType.IDENTIFIER));
  }

  @Test
  public void testConsumKeywords() {
    assertTrue(channel.consum(new CodeReader("KEYWORD1"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyKeywords.KEYWORD1));

    assertTrue(channel.consum(new CodeReader("KeyWord2"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyKeywords.KeyWord2));
  }

  @Test
  public void testNotConsumNumber() {
    assertFalse(channel.consum(new CodeReader("1234"), output));
  }

  private enum MyKeywords implements TokenType {
    KEYWORD1, KeyWord2;

    public String getName() {
      return name();
    }

    public String getValue() {
      return name();
    }

    public boolean hasToBeSkippedFromAst() {
      return false;
    }

  }

}
