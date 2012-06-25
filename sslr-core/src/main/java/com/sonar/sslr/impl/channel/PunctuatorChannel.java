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

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import java.util.Arrays;
import java.util.Comparator;

public class PunctuatorChannel extends Channel<Lexer> {

  public final TokenType[] sortedPunctuators;
  public final char[][] sortedPunctuatorsChars;
  private final Token.Builder tokenBuilder = Token.builder();

  private static class PunctuatorComparator implements Comparator<TokenType> {

    public int compare(TokenType a, TokenType b) {
      if (a.getValue().length() == b.getValue().length()) {
        return 0;
      }
      return a.getValue().length() > b.getValue().length() ? -1 : 1;
    }

  }

  public PunctuatorChannel(TokenType... punctuators) {
    sortedPunctuators = punctuators;
    Arrays.<TokenType> sort(sortedPunctuators, new PunctuatorComparator());

    sortedPunctuatorsChars = new char[sortedPunctuators.length][];

    for (int i = 0; i < sortedPunctuators.length; i++) {
      sortedPunctuatorsChars[i] = sortedPunctuators[i].getValue().toCharArray();
    }
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    for (int i = 0; i < sortedPunctuators.length; i++) {
      if (code.peek() == sortedPunctuatorsChars[i][0]
          && Arrays.equals(code.peek(sortedPunctuatorsChars[i].length), sortedPunctuatorsChars[i])) {

        Token token = tokenBuilder
            .setType(sortedPunctuators[i])
            .setValueAndOriginalValue(sortedPunctuators[i].getValue())
            .setURI(lexer.getURI())
            .setLine(code.getLinePosition())
            .setColumn(code.getColumnPosition())
            .build();

        lexer.addToken(token);

        /* Advance the CodeReader stream by the length of the punctuator */
        for (int j = 0; j < sortedPunctuatorsChars[i].length; j++) {
          code.pop();
        }

        return true;
      }
    }

    return false;
  }

}
