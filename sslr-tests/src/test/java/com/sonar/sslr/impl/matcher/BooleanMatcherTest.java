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
package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class BooleanMatcherTest {

  @Test
  public void ok() {
    assertThat(isTrue(), match("hehe"));
    assertThat(isFalse(), org.hamcrest.Matchers.not(match("hehe")));
  }

  @Test
  public void testToString() {
    assertEquals(isTrue().toString(), "isTrue()");
    assertEquals(isFalse().toString(), "isFalse()");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(isTrue() == isTrue(), is(true));
    assertThat(isFalse() == isFalse(), is(true));
    assertThat(isTrue() == isFalse(), is(false));
    assertThat(anyToken() == tillNewLine(), is(false));
  }

}
