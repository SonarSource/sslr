/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.LexerException;

public class RegexpChannel extends Channel<Lexer> {

  private final StringBuilder tmpBuilder = new StringBuilder();
  private final TokenType type;
  private final Matcher matcher;
  private final String regexp;

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

        Token token = Token.builder()
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
