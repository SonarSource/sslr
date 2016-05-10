/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.test.minic.rules;

import com.sonar.sslr.test.minic.MiniCGrammar;
import org.junit.Before;
import org.junit.Test;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class ExpressionTest extends RuleTest {

  @Override
  @Before
  public void init() {
    p.setRootRule(g.rule(MiniCGrammar.EXPRESSION));
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
  }

}
