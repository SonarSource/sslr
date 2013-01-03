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
package org.sonar.sslr.internal.text;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PlainTextTest {

  private PlainText text;

  @Before
  public void setUp() {
    text = new PlainText("bar".toCharArray());
  }

  @Test
  public void test_getText() {
    assertThat(text.sequence().getText()).isSameAs(text);
  }

  @Test
  public void test_length() {
    assertThat(text.length()).isEqualTo(3);
  }

  @Test
  public void test_subText() {
    assertThat(text.subText(1, 2)).isInstanceOf(SubText.class);
  }

  @Test
  public void test_charAt() {
    assertThat(text.charAt(0)).isEqualTo('b');
    assertThat(text.charAt(1)).isEqualTo('a');
    assertThat(text.charAt(2)).isEqualTo('r');
  }

  @Test
  public void test_getLocation() {
    assertThat(text.getLocation(0)).isNull();
  }

  @Test
  public void test_getCursor() {
    assertThat(text.sequence()).isSameAs(text);
  }

  @Test
  public void test_getTransformationDepth() {
    assertThat(text.getTransformationDepth()).isEqualTo(0);
  }

}
