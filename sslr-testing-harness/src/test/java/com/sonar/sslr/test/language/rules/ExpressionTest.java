/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.language.rules;

import static com.sonar.sslr.test.parser.ParserMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ExpressionTest extends RuleTest {

  @Override
  @Before
  public void init() {
    p.setRootRule(g.expression);
  }

  @Test
  public void reallife() {
    assertThat(p, parse("1"));
    assertThat(p, parse("1 + 1"));
    assertThat(p, parse("1 + 1 * 1"));
    assertThat(p, parse("(1)"));
    assertThat(p, parse("(1 + 1)"));
    assertThat(p, parse("myVariable"));
    assertThat(p, parse("myVariable = 0"));
    assertThat(p, notParse("myVariable = myVariable2 = 0"));
    assertThat(p, parse("myFunction()"));
    assertThat(p, parse("myFunction(arg1, arg2, 1*3)"));
    assertThat(p, parse("myVariable++"));
    assertThat(p, parse("++myVariable"));
    assertThat(p, notParse("++++myVariable"));
    assertThat(p, parse("myVariable = i++"));
    assertThat(p, parse("myVariable = myFunction(1, 3)*2"));
    assertThat(p, parse("++((myVariable))"));
  }

}
