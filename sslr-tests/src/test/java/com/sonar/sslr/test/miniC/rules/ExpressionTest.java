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
package com.sonar.sslr.test.miniC.rules;

import org.junit.Before;
import org.junit.Test;

import static com.sonar.sslr.test.parser.ParserMatchers.notParse;
import static com.sonar.sslr.test.parser.ParserMatchers.parse;
import static org.junit.Assert.assertThat;
import static org.sonar.sslr.tests.Assertions.assertThat;

public class ExpressionTest extends RuleTest {

  @Override
  @Before
  public void init() {
    p.setRootRule(g.expression);
  }

  @Test
  public void reallife() {
    assertThat(p)
        .matches("1")
        .matches("1 + 1")
        .matches("1 + 1 * 1")
        .matches("(1)")
        .matches("myVariable")
        .matches("myVariable = 0")
        .notMatches("myVariable = myVariable2 = 0")
        .matches("myFunction()")
        .matches("myFunction(arg1, arg2, 1*3)")
        .matches("myVariable++")
        .matches("++myVariable")
        .notMatches("++++myVariable")
        .matches("myVariable = i++")
        .matches("myVariable = myFunction(1, 3)*2")
        .matches("++((myVariable))");

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
