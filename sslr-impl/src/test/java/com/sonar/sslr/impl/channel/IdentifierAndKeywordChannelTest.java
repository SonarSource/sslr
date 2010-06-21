/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.sonar.test.channel.ChannelMatchers.consume;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class IdentifierAndKeywordChannelTest {

  private IdentifierAndKeywordChannel channel;
  private LexerOutput output = new LexerOutput();

  @Test
  public void testConsumeWord() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", true, MyKeywords.values());
    assertThat(channel, consume("word", output));
    assertThat(output, hasToken("word", GenericTokenType.IDENTIFIER));
  }

  @Test
  public void testConsumeCaseSensitiveKeywords() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", true, MyKeywords.values());
    assertThat(channel, consume("KEYWORD1", output));
    assertThat(output, hasToken("KEYWORD1", MyKeywords.KEYWORD1));

    assertThat(channel, consume("KeyWord2", output));
    assertThat(output, hasToken("KeyWord2", MyKeywords.KeyWord2));

    assertThat(channel, consume("KEYWORD2", output));
    assertThat(output, hasToken("KEYWORD2", GenericTokenType.IDENTIFIER));
  }

  @Test
  public void testConsumeNotCaseSensitiveKeywords() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", false, MyKeywords.values());
    assertThat(channel, consume("keyword1", output));
    assertThat(output, hasToken("KEYWORD1", MyKeywords.KEYWORD1));

    assertThat(channel, consume("keyword2", output));
    assertThat(output, hasToken("KEYWORD2", MyKeywords.KeyWord2));
  }
  
  @Test
  public void testColumnAndLineNumbers() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", false, MyKeywords.values());
    CodeReader reader = new CodeReader("\n\n  keyword1");
    reader.pop();
    reader.pop();
    reader.pop();
    reader.pop();
    assertThat(channel, consume(reader, output));
    Token keyword = output.getTokens().get(0);
    assertThat(keyword.getColumn(), is(2));
    assertThat(keyword.getLine(), is(3));
  }

  @Test
  public void testNotConsumNumber() {
    assertThat(channel, not(consume("1234", output)));
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
