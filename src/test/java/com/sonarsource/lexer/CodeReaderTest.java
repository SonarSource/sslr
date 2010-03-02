/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

public class CodeReaderTest {

  @Test
  public void pop() {
    CodeReader reader = new CodeReader(new StringReader("package org.sonar;"));

    StringWriter sw = new StringWriter();
    reader.pop(sw);
    assertEquals("p", sw.toString());
    reader.pop(sw);
    assertEquals("pa", sw.toString());
  }

  @Test
  public void peekAndPop() {
    CodeReader reader = new CodeReader(new StringReader("package org.sonar;"));
    StringWriter sw = new StringWriter();
    assertEquals('p', (char)reader.peek());
    reader.pop(sw);
    assertEquals("p", sw.toString());
    assertEquals('p', reader.lastChar());
    assertEquals('a', reader.peek());
    assertEquals('p', reader.lastChar());
    reader.pop(sw);
    assertEquals("pa", sw.toString());
  }

  @Test
  public void peekLast() {
    CodeReader reader = new CodeReader(new StringReader("bar"));
    StringWriter sw = new StringWriter();
    reader.pop(sw);
    assertEquals("b", sw.toString());
    reader.pop(sw);
    assertEquals("ba", sw.toString());
    reader.pop(sw);
    assertEquals("bar", sw.toString());
    assertEquals(-1, reader.peek());
  }

  @Test
  public void peekToEnd() {
    CodeReader reader = new CodeReader(new StringReader("bar"));
    char[] chars = reader.peek(4);
    assertThat(chars.length, is(4));
    assertThat((int) chars[3], lessThan(1));
  }
}
