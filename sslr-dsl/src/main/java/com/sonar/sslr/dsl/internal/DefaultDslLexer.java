/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import static com.sonar.sslr.dsl.DslTokenType.DOUBLE;
import static com.sonar.sslr.dsl.DslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DslTokenType.PUNCTUATOR;
import static com.sonar.sslr.dsl.DslTokenType.WORD;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.RegexpChannel;

public class DefaultDslLexer {

  private DefaultDslLexer() {

  }

  public static Lexer create() {
    return Lexer.builder().withFailIfNoChannelToConsumeOneCharacter(true)
    .withChannel(new RegexpChannel(WORD, "\\p{Alpha}[\\p{Alpha}\\d_]++")) 
    .withChannel(new RegexpChannel(DOUBLE, "\\d++\\.\\d++"))
    .withChannel(new RegexpChannel(INTEGER, "\\d++"))
    .withChannel(new RegexpChannel(LITERAL, "\"([^\\\\\"]|\\\\.)*\""))
    .withChannel(new RegexpChannel(LITERAL, "'([^\\\\']|\\\\.)*'"))
    .withChannel(new BlackHoleChannel("[\\s]"))
    .withChannel(new RegexpChannel(PUNCTUATOR, ".")).build();
  }
}
