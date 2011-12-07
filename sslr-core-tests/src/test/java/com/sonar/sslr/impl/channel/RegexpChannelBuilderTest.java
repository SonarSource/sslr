/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
