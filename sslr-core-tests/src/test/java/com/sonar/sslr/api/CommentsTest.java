/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class CommentsTest {

  Comments comments;

  private static Token newCommentToken(String value, int line, int column) {
    return new Token(GenericTokenType.COMMENT, value, line, column);
  }

  @Before
  public void init() {
    ListMultimap<Integer, Token> list = LinkedListMultimap.<Integer, Token> create();

    list.put(1, newCommentToken("hehe", 1, 0));
    list.put(1, newCommentToken("    ", 1, 1));
    list.put(1, newCommentToken("b", 1, 2));

    list.put(4, newCommentToken("\n\n", 4, 0));

    list.put(10, newCommentToken("    ", 10, 0));
    list.put(10, newCommentToken("comment1\ncomment2", 10, 1));

    list.put(15, newCommentToken("", 15, 0));
    list.put(16, newCommentToken("z", 16, 0));

    comments = new Comments(list, new DefaultCommentAnalyser());
  }

  @Test
  public void iterator() {
    Iterator<Token> iterator = comments.iterator();

    assertEquals(iterator.next(), newCommentToken("hehe", 1, 0));
    assertEquals(iterator.next(), newCommentToken("    ", 1, 1));
    assertEquals(iterator.next(), newCommentToken("b", 1, 2));

    assertEquals(iterator.next(), newCommentToken("\n\n", 4, 0));

    assertEquals(iterator.next(), newCommentToken("    ", 10, 0));
    assertEquals(iterator.next(), newCommentToken("comment1\ncomment2", 10, 1));

    assertEquals(iterator.next(), newCommentToken("", 15, 0));
    assertEquals(iterator.next(), newCommentToken("z", 16, 0));

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
    assertThat(comments.getCommentTokensAtLine(1), hasItem(newCommentToken("hehe", 1, 0)));
    assertThat(comments.getCommentTokensAtLine(1), hasItem(newCommentToken("    ", 1, 1)));
    assertThat(comments.getCommentTokensAtLine(1), hasItem(newCommentToken("b", 1, 2)));
    assertThat(comments.getCommentTokensAtLine(1), not(hasItem(newCommentToken("\n\n", 4, 0))));

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

    public boolean isBlank(String commentLine) {
      for (int i = 0; i < commentLine.length(); i++) {
        if (Character.isLetterOrDigit(commentLine.charAt(i)))
          return false;
      }

      return true;
    }
  }

}
