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
package com.sonar.sslr.test.miniC;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ParsingEventListener;

public final class MiniCParser {

  private static final Parser<MiniCGrammar> P = MiniCParser.create();
  private static final MiniCGrammar G = P.getGrammar();

  private MiniCParser() {
  }

  public static Parser<MiniCGrammar> create(ParsingEventListener... parsingEventListeners) {
    return Parser.builder(new MiniCGrammar()).withLexer(MiniCLexer.create()).setParsingEventListeners(parsingEventListeners).build();
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

  public static MiniCGrammar getGrammar() {
    return G;
  }

}
