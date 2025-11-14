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
package com.sonar.sslr.test.minic;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.apache.commons.io.FileUtils;

import java.io.File;

public final class MiniCParser {

  private static final Parser<Grammar> P = MiniCParser.create();

  private MiniCParser() {
  }

  public static Parser<Grammar> create() {
    return Parser.builder(MiniCGrammar.create()).withLexer(MiniCLexer.create()).build();
  }

  public static AstNode parseFile(String filePath) {
    File file = FileUtils.toFile(MiniCParser.class.getResource(filePath));
    if (file == null || !file.exists()) {
      throw new AssertionError("The file \"" + filePath + "\" does not exist.");
    }

    return P.parse(file);
  }

  public static AstNode parseString(String source) {
    return P.parse(source);
  }

}
