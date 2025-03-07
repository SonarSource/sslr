/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
