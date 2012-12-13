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

import org.junit.Test;
import org.sonar.sslr.text.Text;

import static org.fest.assertions.Assertions.assertThat;

public class AbstractTextOperationsTest {

  @Test
  public void startsWith() {
    assertThat(new StringText("").startsWith("foo")).isFalse();
    assertThat(new StringText("foobar").startsWith("bar")).isFalse();
    assertThat(new StringText("foo").startsWith("foobar")).isFalse();

    assertThat(new StringText("foo").startsWith("f")).isTrue();
    assertThat(new StringText("foo").startsWith("fo")).isTrue();
    assertThat(new StringText("foo").startsWith("foo")).isTrue();
  }

  @Test
  public void isEmpty() {
    assertThat(new StringText("").isEmpty()).isTrue();
    assertThat(new StringText("foo").isEmpty()).isFalse();
  }

  private static class StringText extends AbstractTextOperations {

    private final String string;

    public StringText(String string) {
      this.string = string;
    }

    public int length() {
      return string.length();
    }

    public char charAt(int index) {
      return string.charAt(index);
    }

    public Text subSequence(int start, int end) {
      return null;
    }

  }

}
