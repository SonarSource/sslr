/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.lexer.LexerMatchers.consume;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.TokenType;

public class IdentifierAndKeywordChannelTest {

  private IdentifierAndKeywordChannel channel;
  private LexerOutput output = new LexerOutput();

  @Test
  public void testConsumeWord() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", true, MyKeywords.values());
    assertThat(channel, consume(new CodeReader("word"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) GenericTokenType.IDENTIFIER));
  }

  @Test
  public void testConsumeCaseSensitiveKeywords() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", true, MyKeywords.values());
    assertThat(channel, consume(new CodeReader("KEYWORD1"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyKeywords.KEYWORD1));

    assertThat(channel, consume(new CodeReader("KeyWord2"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyKeywords.KeyWord2));
    
    assertThat(channel, consume(new CodeReader("KEYWORD2"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) GenericTokenType.IDENTIFIER));
  }
  
  @Test
  public void testConsumeNotCaseSensitiveKeywords() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", false, MyKeywords.values());
    assertThat(channel, consume(new CodeReader("keyword1"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyKeywords.KEYWORD1));

    assertThat(channel, consume(new CodeReader("keyword2"), output));
    assertThat(output.getLastToken().getType(), is((TokenType) MyKeywords.KeyWord2));
  }

  @Test
  public void testNotConsumNumber() {
    assertThat(channel, not(consume(new CodeReader("1234"), output)));
  }

  private enum MyKeywords implements TokenType {
    KEYWORD1, KeyWord2;

    public String getName() {
      return name();
    }

    public String getValue() {
      return name();
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
      return false;
    }

  }

}
