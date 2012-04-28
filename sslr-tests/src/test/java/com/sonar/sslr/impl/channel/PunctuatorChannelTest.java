/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
