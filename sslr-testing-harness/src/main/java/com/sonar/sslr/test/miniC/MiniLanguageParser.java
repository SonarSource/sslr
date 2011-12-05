/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ParsingEventListener;

public final class MiniLanguageParser {

  private MiniLanguageParser() {
  }

  public static Parser<MiniLanguageGrammar> create(ParsingEventListener... parsingEventListeners) {
    return Parser.builder(new MiniLanguageGrammar()).withLexer(MiniLanguageLexer.getLexer())
        .withParsingEventListeners(parsingEventListeners).build();
  }

}
