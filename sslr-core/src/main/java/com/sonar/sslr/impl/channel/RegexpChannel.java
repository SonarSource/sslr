/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.channel;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.LexerException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates token of specified type from characters, which match given regular expression.
 *
 * @see RegexpChannelBuilder
 */
public class RegexpChannel extends Channel<Lexer> {

  private final StringBuilder tmpBuilder = new StringBuilder();
  private final TokenType type;
  private final Matcher matcher;
  private final String regexp;
  private final Token.Builder tokenBuilder = Token.builder();

  /**
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public RegexpChannel(TokenType type, String regexp) {
    matcher = Pattern.compile(regexp).matcher("");
    this.type = type;
    this.regexp = regexp;
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    try {
      if (code.popTo(matcher, tmpBuilder) > 0) {
        String value = tmpBuilder.toString();

        Token token = tokenBuilder
            .setType(type)
            .setValueAndOriginalValue(value)
            .setURI(lexer.getURI())
            .setLine(code.getPreviousCursor().getLine())
            .setColumn(code.getPreviousCursor().getColumn())
            .build();

        lexer.addToken(token);

        tmpBuilder.delete(0, tmpBuilder.length());
        return true;
      }
      return false;
    } catch (StackOverflowError e) {
      throw new LexerException(
          "The regular expression "
              + regexp
              + " has led to a stack overflow error. "
              + "This error is certainly due to an inefficient use of alternations. See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5050507",
          e);
    }
  }
}
