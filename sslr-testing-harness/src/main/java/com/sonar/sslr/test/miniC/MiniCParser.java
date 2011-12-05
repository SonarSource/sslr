/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ParsingEventListener;

public final class MiniCParser {

  private MiniCParser() {
  }

  public static Parser<MiniCGrammar> create(ParsingEventListener... parsingEventListeners) {
    return Parser.builder(new MiniCGrammar()).withLexer(MiniCLexer.getLexer())
        .withParsingEventListeners(parsingEventListeners).build();
  }

}
