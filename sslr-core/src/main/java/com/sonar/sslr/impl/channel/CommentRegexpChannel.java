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

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.LexerException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sonar.sslr.api.GenericTokenType.COMMENT;

/**
 * Creates comment trivia from characters, which match given regular expression.
 *
 * @see RegexpChannelBuilder
 */
public class CommentRegexpChannel extends Channel<Lexer> {

  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;
  private final String regexp;
  private final Token.Builder tokenBuilder = Token.builder();

  public CommentRegexpChannel(String regexp) {
    matcher = Pattern.compile(regexp).matcher("");
    this.regexp = regexp;
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    try {
      if (code.popTo(matcher, tmpBuilder) > 0) {
        String value = tmpBuilder.toString();

        Token token = tokenBuilder
            .setType(COMMENT)
            .setValueAndOriginalValue(value)
            .setURI(lexer.getURI())
            .setLine(code.getPreviousCursor().getLine())
            .setColumn(code.getPreviousCursor().getColumn())
            .build();

        lexer.addTrivia(Trivia.createComment(token));

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
