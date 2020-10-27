/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
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
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import org.junit.Test;
import org.sonar.sslr.channel.CodeReader;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static com.sonar.sslr.test.lexer.MockHelper.mockLexer;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.sonar.sslr.test.channel.ChannelMatchers.consume;

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

    assertThat(channel.consume(new CodeReader("!"), lexer)).isFalse();
  }

  @Test
  public void testNotConsumeWord() {
    assertThat(channel.consume(new CodeReader("word"), lexer)).isFalse();
  }

  private enum MyPunctuatorAndOperator implements TokenType {
    STAR("*"), COLON(","), EQUAL("="), EQUAL_OP("=="), MUL_ASSIGN("*="), NOT_EQUAL("!=");

    private final String value;

    private MyPunctuatorAndOperator(String value) {
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
}
