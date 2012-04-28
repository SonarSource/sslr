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
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class RegexpChannelBuilderTest {

  @Test
  public void testOpt() {
    assertThat(opt("L"), equalTo("L?+"));
  }

  @Test
  public void testOne2n() {
    assertThat(one2n("L"), equalTo("L++"));
  }

  @Test
  public void testO2n() {
    assertThat(o2n("L"), equalTo("L*+"));
  }

  @Test
  public void testg() {
    assertThat(g("L"), equalTo("(L)"));
    assertThat(g("L", "l"), equalTo("(Ll)"));
  }

  @Test
  public void testOr() {
    assertThat(or("L", "l", "U", "u"), equalTo("(L|l|U|u)"));
  }

  @Test
  public void testAnyButNot() {
    assertThat(anyButNot("L", "l"), equalTo("[^Ll]"));
  }

}
