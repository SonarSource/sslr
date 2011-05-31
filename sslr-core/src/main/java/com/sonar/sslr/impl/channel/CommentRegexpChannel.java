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

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.LexerException;

public class CommentRegexpChannel extends Channel<LexerOutput> {

  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;
  private final String regexp;
  private final int removeBefore;
  private final int removeAfter;
  private final boolean trimBeforeRemove;
  private final boolean trimAfterRemove;

  public CommentRegexpChannel(String regexp) {
  	this(regexp, 0, 0, false, false);
  }
  
  public CommentRegexpChannel(String regexp, boolean trimBeforeRemove, boolean trimAfterRemove) {
  	this(regexp, 0, 0, trimBeforeRemove, trimAfterRemove);
  }
  
  public CommentRegexpChannel(String regexp, int removeBefore, int removeAfter) {
  	this(regexp, removeBefore, removeAfter, false, false);
  }
  
  public CommentRegexpChannel(String regexp, int removeBefore, int removeAfter, boolean trimBeforeRemove, boolean trimAfterRemove) {
    matcher = Pattern.compile(regexp).matcher("");
    this.regexp = regexp;
    this.removeBefore = removeBefore;
    this.removeAfter = removeAfter;
    this.trimBeforeRemove = trimBeforeRemove;
    this.trimAfterRemove = trimAfterRemove;
  }

  @Override
  public boolean consume(CodeReader code, LexerOutput output) {
    try {
      if (code.popTo(matcher, tmpBuilder) > 0) {
        String value = tmpBuilder.toString();
        
        if (trimBeforeRemove) {
        	value = value.trim();
        }
        
        if (removeBefore > 0 || removeAfter > 0) {
        	value = value.substring(removeBefore, value.length() - removeAfter);
        }
        
        if (trimAfterRemove) {
        	value = value.trim();
        }

        output.addCommentToken(new Token(GenericTokenType.COMMENT, value, code.getPreviousCursor().getLine(), code.getPreviousCursor().getColumn()));
        
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
