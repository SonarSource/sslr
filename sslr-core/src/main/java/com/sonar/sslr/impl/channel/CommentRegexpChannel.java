/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.api.GenericTokenType.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.LexerException;

public class CommentRegexpChannel extends Channel<Lexer> {

  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;
  private final String regexp;

  public CommentRegexpChannel(String regexp) {
    matcher = Pattern.compile(regexp).matcher("");
    this.regexp = regexp;
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    try {
      if (code.popTo(matcher, tmpBuilder) > 0) {
        String value = tmpBuilder.toString();

        Token token = Token.builder()
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