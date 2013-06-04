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
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.Grammar;
import org.junit.Test;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class JsonGrammarTest {

  private Grammar g = JsonGrammar.create();

  @Test
  public void whitespace() {
    assertThat(g.rule(JsonGrammar.WHITESPACE))
      .matches(" \n\r\t\f");
  }

  @Test
  public void number() {
    assertThat(g.rule(JsonGrammar.NUMBER))
      .matches("1234567890")
      .matches("-1234567890")
      .matches("0")
      .notMatches("01")
      .matches("0.0123456789")
      .matches("1E2")
      .matches("1e2")
      .matches("1E+2")
      .matches("1E-2");
  }

  @Test
  public void string() {
    assertThat(g.rule(JsonGrammar.STRING))
      .matches("\"\"")
      .matches("\"\\\"\"")
      .matches("\"\\\\\"")
      .matches("\"\\/\"")
      .matches("\"\\b\"")
      .matches("\"\\f\"")
      .matches("\"\\n\"")
      .matches("\"\\r\"")
      .matches("\"\\t\"")
      .matches("\"\\uFFFF\"")
      .matches("\"string\"");
  }

  @Test
  public void value() {
    assertThat(g.rule(JsonGrammar.VALUE))
      .matches("\"string\"")
      .matches("{}")
      .matches("[]")
      .matches("42")
      .matches("true")
      .matches("false")
      .matches("null");
  }

  @Test
  public void object() {
    assertThat(g.rule(JsonGrammar.OBJECT))
      .matches("{ }")
      .matches("{ \"string\" : true }")
      .matches("{ \"string\" : true, \"string\" : false }");
  }

  @Test
  public void array() {
    assertThat(g.rule(JsonGrammar.ARRAY))
      .matches("[ ]")
      .matches("[ true ]")
      .matches("[ true, false ]");
  }

  @Test
  public void json() {
    assertThat(g.rule(JsonGrammar.JSON))
      .matches("{}")
      .matches("[]");
  }

}
