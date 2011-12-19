/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.util.Arrays;
import java.util.Comparator;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.TokenType;

public class PunctuatorChannel extends Channel<LexerOutput> {

  public final TokenType[] sortedPunctuators;
  public final char[][] sortedPunctuatorsChars;

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
  public boolean consume(CodeReader code, LexerOutput output) {
    for (int i = 0; i < sortedPunctuators.length; i++) {
      if (code.peek() == sortedPunctuatorsChars[i][0]
          && Arrays.equals(code.peek(sortedPunctuatorsChars[i].length), sortedPunctuatorsChars[i])) {
        /* The punctuator matched, add the token to the lexer output */
        output.addTokenAndProcess(sortedPunctuators[i], sortedPunctuators[i].getValue(), code.getLinePosition(), code.getColumnPosition());

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
