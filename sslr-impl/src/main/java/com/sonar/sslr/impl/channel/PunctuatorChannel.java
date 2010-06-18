/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.TokenType;

public class PunctuatorChannel extends Channel<LexerOutput> {

  public Multimap<Character, TokenType> specialChars;

  public PunctuatorChannel(TokenType... characters) {
    specialChars = HashMultimap.create();
    for (TokenType specialChar : characters) {
      specialChars.put(Character.valueOf(specialChar.getValue().charAt(0)), specialChar);
    }
  }

  @Override
  public boolean consume(CodeReader code, LexerOutput output) {
    Character nextChar = Character.valueOf((char) code.peek());
    if (specialChars.containsKey(nextChar)) {
      EndSpecialCharsMatcher matcher = new EndSpecialCharsMatcher(specialChars.get(nextChar));
      code.popTo(matcher, new EmptyAppendable());
      output.addTokenAndProcess(matcher.getSpecialchar(), matcher.getSpecialchar().getValue(), code.getLinePosition(), code
          .getColumnPosition());
      return true;
    }
    return false;
  }

  private class EndSpecialCharsMatcher implements EndMatcher {

    private final Collection<TokenType> matchtingChars;
    private final Collection<TokenType> specialCharsToRemove = new ArrayList<TokenType>();
    private TokenType specialChar;
    private int index = 0;

    public EndSpecialCharsMatcher(Collection<TokenType> matchtingChars) {
      this.matchtingChars = new ArrayList<TokenType>(matchtingChars);
    }

    public boolean match(int nextChar) {
      index++;
      for (TokenType tokenType : matchtingChars) {
        if (tokenType.getValue().length() < index + 1) {
          specialChar = tokenType;
          specialCharsToRemove.add(tokenType);
        } else if (tokenType.getValue().charAt(index) != (char) nextChar) {
          specialCharsToRemove.add(tokenType);
        }
      }
      matchtingChars.removeAll(specialCharsToRemove);
      specialCharsToRemove.clear();
      if (matchtingChars.size() == 0) {
        return true;
      }
      return false;
    }

    public TokenType getSpecialchar() {
      return specialChar;
    }

  }

  private static class EmptyAppendable implements Appendable {

    public Appendable append(CharSequence csq) throws IOException {
      return this;
    }

    public Appendable append(char c) throws IOException {
      return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      return this;
    }

  }
}
