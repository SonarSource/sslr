/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC.rules;

import org.junit.Before;
import org.junit.Test;

import static com.sonar.sslr.test.parser.ParserMatchers.parse;
import static org.junit.Assert.assertThat;

public class StructTest extends RuleTest {

  @Override
  @Before
  public void init() {
    p.setRootRule(g.structDefinition);
  }

  @Test
  public void reallife() {
    assertThat(p, parse("struct my { int a; }"));
    assertThat(p, parse("struct my { int a; int b; }"));
  }

}
