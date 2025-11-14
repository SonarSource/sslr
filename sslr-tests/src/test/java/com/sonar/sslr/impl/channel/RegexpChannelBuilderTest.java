/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
