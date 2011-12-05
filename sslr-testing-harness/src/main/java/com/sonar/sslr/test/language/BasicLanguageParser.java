/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.language;

import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ParsingEventListener;

public final class BasicLanguageParser {

  private BasicLanguageParser() {
  }

  public static Parser<BasicLanguageGrammar> create(ParsingEventListener... parsingEventListeners) {
    return Parser.builder(new BasicLanguageGrammar()).withLexer(BasicLanguageLexer.getLexer())
        .withParsingEventListeners(parsingEventListeners).build();
  }

}
