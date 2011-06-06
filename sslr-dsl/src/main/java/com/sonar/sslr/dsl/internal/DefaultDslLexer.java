/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.RegexpChannel;

import static com.sonar.sslr.dsl.DslTokenType.DOUBLE;
import static com.sonar.sslr.dsl.DslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DslTokenType.PUNCTUATOR;
import static com.sonar.sslr.dsl.DslTokenType.WORD;

public class DefaultDslLexer {

  private DefaultDslLexer() {

  }

  public static Lexer create() {
    return Lexer.builder().optFailIfNoChannelToConsumeOneCharacter().addChannel(new RegexpChannel(WORD, "\\p{Alpha}[\\p{Alpha}\\d_]+"))
        .addChannel(new RegexpChannel(DOUBLE, "\\d++\\.\\d++")).addChannel(new RegexpChannel(INTEGER, "\\d++"))
        .addChannel(new RegexpChannel(LITERAL, "\".*?\"")).addChannel(new RegexpChannel(LITERAL, "'.*?'"))
        .addChannel(new BlackHoleChannel("[\\s]")).addChannel(new RegexpChannel(PUNCTUATOR, ".")).build();
  }
}
