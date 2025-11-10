/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package com.sonar.sslr.impl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognitionException;
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.test.minic.MiniCParser.parseFile;
import static com.sonar.sslr.test.minic.MiniCParser.parseString;
import static org.fest.assertions.Assertions.assertThat;

public class ParserTest {

  @Test(expected = RecognitionException.class)
  public void lexerErrorStringWrappedInRecognitionException() {
    parseString(".");
  }

  @Test(expected = RecognitionException.class)
  public void lexerErrorFileWrappedInRecognitionException() {
    parseFile("/OwnExamples/lexererror.mc");
  }

  @Test
  public void parse() {
    AstNode compilationUnit = parseString("");
    assertThat(compilationUnit.getNumberOfChildren()).isEqualTo(1);
    assertThat(compilationUnit.getFirstChild().is(EOF)).isTrue();
  }

}
