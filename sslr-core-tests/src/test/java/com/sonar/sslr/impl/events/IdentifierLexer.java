/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.COMMENT;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;

public class IdentifierLexer {

  private IdentifierLexer() {

  }

  public static Lexer create() {
    return Lexer.builder().withChannel(regexp(COMMENT, "!COMMENT!"))
        .withChannel(new IdentifierAndKeywordChannel("[a-zA-Z][a-zA-Z0-9]*", true)).withChannel(new BlackHoleChannel("[ \t\r\n]+"))
        .withFailIfNoChannelToConsumeOneCharacter(true).build();
  }
}
