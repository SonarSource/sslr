/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
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
package org.sonar.sslr.channel;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;

public class CodeReaderTest {

  @Test
  public void testPopWithAppendable() {
    CodeReader reader = new CodeReader("package org.sonar;");

    StringBuilder sw = new StringBuilder();
    reader.pop(sw);
    assertEquals("p", sw.toString());
    reader.pop(sw);
    assertEquals("pa", sw.toString());

  }

  @Test
  public void testPeekACharArray() {
    CodeReader reader = new CodeReader(new StringReader("bar"));
    char[] chars = reader.peek(2);
    assertThat(chars.length, is(2));
    assertThat(chars[0], is('b'));
    assertThat(chars[1], is('a'));
  }

  @Test
  public void testPeekTo() {
    CodeReader reader = new CodeReader(new StringReader("package org.sonar;"));
    StringBuilder result = new StringBuilder();
    reader.peekTo(new EndMatcher() {

      @Override
      public boolean match(int endFlag) {
        return 'r' == (char) endFlag;
      }
    }, result);
    assertEquals("package o", result.toString());
    assertThat(reader.peek(), is((int) 'p')); // never called pop()
  }

  @Test
  public void peekTo_should_stop_at_end_of_input() {
    CodeReader reader = new CodeReader("foo");
    StringBuilder result = new StringBuilder();
    reader.peekTo(i -> false, result);
    assertEquals("foo", result.toString());
  }

  @Test
  public void testPopToWithRegex() {
    CodeReader reader = new CodeReader(new StringReader("123ABC"));
    StringBuilder token = new StringBuilder();
    assertEquals(3, reader.popTo(Pattern.compile("\\d+").matcher(new String()), token));
    assertEquals("123", token.toString());
    assertEquals(-1, reader.popTo(Pattern.compile("\\d+").matcher(new String()), token));
    assertEquals(3, reader.popTo(Pattern.compile("\\w+").matcher(new String()), token));
    assertEquals("123ABC", token.toString());
    assertEquals(-1, reader.popTo(Pattern.compile("\\w+").matcher(new String()), token));

    // Should reset matcher with empty string:
    Matcher matcher = Pattern.compile("\\d+").matcher("");
    reader.popTo(matcher, token);
    try {
      matcher.find(1);
      Assert.fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      assertEquals("Illegal start index", e.getMessage());
    }
  }

  @Test
  public void testStackOverflowError() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    for (int i = 0; i < 10000; i++) {
      sb.append(Integer.toHexString(i));
    }
    CodeReader reader = new CodeReader(sb.toString());
    reader.pop();
    reader.pop();

    ChannelException thrown = assertThrows(ChannelException.class,
      () -> reader.popTo(Pattern.compile("([a-fA-F]|\\d)+").matcher(""), new StringBuilder()));
    assertEquals("Unable to apply regular expression '([a-fA-F]|\\d)+' at line 2 and column 1," +
        " because it led to a stack overflow error." +
        " This error may be due to an inefficient use of alternations - see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5050507",
      thrown.getMessage());
  }

  @Test
  public void testPopToWithRegexAndFollowingMatcher() {
    Matcher digitMatcher = Pattern.compile("\\d+").matcher(new String());
    Matcher alphabeticMatcher = Pattern.compile("[a-zA-Z]").matcher(new String());
    StringBuilder token = new StringBuilder();
    assertEquals(-1, new CodeReader(new StringReader("123 ABC")).popTo(digitMatcher, alphabeticMatcher, token));
    assertEquals("", token.toString());
    assertEquals(3, new CodeReader(new StringReader("123ABC")).popTo(digitMatcher, alphabeticMatcher, token));
    assertEquals("123", token.toString());
  }
}
