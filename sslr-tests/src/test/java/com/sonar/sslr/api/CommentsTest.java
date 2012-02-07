/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public class CommentsTest {

  private Comments comments;

  private Token comment1;
  private Token comment2;
  private Token comment3;
  private Token comment4;
  private Token comment5;
  private Token comment6;
  private Token comment7;
  private Token comment8;

  private static Token newCommentToken(String value, int line, int column) {
    return mockTokenBuilder(GenericTokenType.COMMENT, value)
        .setLine(line)
        .setColumn(column)
        .build();
  }

  @Before
  public void init() {
    ListMultimap<Integer, Token> list = LinkedListMultimap.<Integer, Token> create();

    comment1 = newCommentToken("hehe", 1, 0);
    list.put(1, comment1);

    comment2 = newCommentToken("    ", 1, 1);
    list.put(1, comment2);

    comment3 = newCommentToken("b", 1, 2);
    list.put(1, comment3);

    comment4 = newCommentToken("\n\n", 4, 0);
    list.put(4, comment4);

    comment5 = newCommentToken("    ", 10, 0);
    list.put(10, comment5);

    comment6 = newCommentToken("comment1\ncomment2", 10, 1);
    list.put(10, comment6);

    comment7 = newCommentToken("", 15, 0);
    list.put(15, comment7);

    comment8 = newCommentToken("z", 16, 0);
    list.put(16, comment8);

    comments = new Comments(list, new DefaultCommentAnalyser());
  }

  @Test
  public void iterator() {
    Iterator<Token> iterator = comments.iterator();

    assertEquals(iterator.next(), comment1);
    assertEquals(iterator.next(), comment2);
    assertEquals(iterator.next(), comment3);
    assertEquals(iterator.next(), comment4);
    assertEquals(iterator.next(), comment5);
    assertEquals(iterator.next(), comment6);
    assertEquals(iterator.next(), comment7);
    assertEquals(iterator.next(), comment8);

    assertThat(iterator.hasNext(), is(false));
  }

  @Test
  public void size() {
    assertThat(comments.size(), is(8));
  }

  @Test
  public void hasCommentTokensAtLine() {
    assertThat(comments.hasCommentTokensAtLine(1), is(true));
    assertThat(comments.hasCommentTokensAtLine(4), is(true));
    assertThat(comments.hasCommentTokensAtLine(10), is(true));

    assertThat(comments.hasCommentTokensAtLine(0), is(false));
    assertThat(comments.hasCommentTokensAtLine( -100), is(false));
    assertThat(comments.hasCommentTokensAtLine(5), is(false));
    assertThat(comments.hasCommentTokensAtLine(6), is(false));
    assertThat(comments.hasCommentTokensAtLine(7), is(false));
  }

  @Test
  public void getCommentTokensAtLine() {
    assertThat(comments.getCommentTokensAtLine(1).size(), is(3));
    assertThat(comments.getCommentTokensAtLine(1), hasItem(comment1));
    assertThat(comments.getCommentTokensAtLine(1), hasItem(comment2));
    assertThat(comments.getCommentTokensAtLine(1), hasItem(comment3));
    assertThat(comments.getCommentTokensAtLine(1), not(hasItem(comment4)));

    assertThat(comments.getCommentTokensAtLine(2).size(), is(0));
    assertThat(comments.getCommentTokensAtLine(4).size(), is(1));
    assertThat(comments.getCommentTokensAtLine(5).size(), is(0));
    assertThat(comments.getCommentTokensAtLine(6).size(), is(0));
  }

  @Test
  public void isThereCommentBeforeLine() {
    assertThat(comments.isThereCommentBeforeLine(1), is(false));
    assertThat(comments.isThereCommentBeforeLine(2), is(true));
    assertThat(comments.isThereCommentBeforeLine(3), is(false));
    assertThat(comments.isThereCommentBeforeLine(1), is(false));

    assertThat(comments.isThereCommentBeforeLine(12), is(false));

    assertThat(comments.isThereCommentBeforeLine(16), is(false));
    assertThat(comments.isThereCommentBeforeLine(17), is(true));
  }

  private class DefaultCommentAnalyser extends CommentAnalyser {

    @Override
    public boolean isBlank(String commentLine) {
      for (int i = 0; i < commentLine.length(); i++) {
        if (Character.isLetterOrDigit(commentLine.charAt(i))) {
          return false;
        }
      }

      return true;
    }

    @Override
    public String getContents(String comment) {
      return comment;
    }

  }

}
