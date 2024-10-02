/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.impl.channel;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import org.junit.Test;
import org.sonar.sslr.channel.CodeReader;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasOriginalToken;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static com.sonar.sslr.test.lexer.MockHelper.mockLexer;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.sonar.sslr.test.channel.ChannelMatchers.consume;

public class IdentifierAndKeywordChannelTest {

  private IdentifierAndKeywordChannel channel;
  private final Lexer lexer = mockLexer();

  @Test
  public void testConsumeWord() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", true, MyKeywords.values());
    assertThat(channel, consume("word", lexer));
    assertThat(lexer.getTokens(), hasToken("word", GenericTokenType.IDENTIFIER));
  }

  @Test
  public void testConsumeCaseSensitiveKeywords() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", true, MyKeywords.values());
    assertThat(channel, consume("KEYWORD1", lexer));
    assertThat(lexer.getTokens(), hasToken("KEYWORD1", MyKeywords.KEYWORD1));

    assertThat(channel, consume("KeyWord2", lexer));
    assertThat(lexer.getTokens(), hasToken("KeyWord2", MyKeywords.KeyWord2));

    assertThat(channel, consume("KEYWORD2", lexer));
    assertThat(lexer.getTokens(), hasToken("KEYWORD2", GenericTokenType.IDENTIFIER));
  }

  @Test
  public void testConsumeNotCaseSensitiveKeywords() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", false, MyKeywords.values());
    assertThat(channel, consume("keyword1", lexer));
    assertThat(lexer.getTokens(), hasToken("KEYWORD1", MyKeywords.KEYWORD1));
    assertThat(lexer.getTokens(), hasToken("KEYWORD1"));
    assertThat(lexer.getTokens(), hasOriginalToken("keyword1"));

    assertThat(channel, consume("keyword2", lexer));
    assertThat(lexer.getTokens(), hasToken("KEYWORD2", MyKeywords.KeyWord2));
  }

  @Test
  public void testColumnAndLineNumbers() {
    channel = new IdentifierAndKeywordChannel("[a-zA-Z_][a-zA-Z_0-9]*", false, MyKeywords.values());
    CodeReader reader = new CodeReader("\n\n  keyword1");
    reader.pop();
    reader.pop();
    reader.pop();
    reader.pop();
    assertThat(channel, consume(reader, lexer));
    Token keyword = lexer.getTokens().get(0);
    assertThat(keyword.getColumn(), is(2));
    assertThat(keyword.getLine(), is(3));
  }

  @Test
  public void testNotConsumNumber() {
    assertThat(channel, not(consume("1234", lexer)));
  }

  private enum MyKeywords implements TokenType {
    KEYWORD1, KeyWord2;

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

}
