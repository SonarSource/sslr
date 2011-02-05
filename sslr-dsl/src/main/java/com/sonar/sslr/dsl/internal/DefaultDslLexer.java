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

import java.util.ArrayList;
import java.util.List;

import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.RegexpChannel;

public class DefaultDslLexer extends Lexer {

  @Override
  protected ChannelDispatcher<LexerOutput> getChannelDispatcher() {
    List<Channel> channels = new ArrayList<Channel>();
    channels.add(new RegexpChannel(WORD, "\\p{Alpha}[\\p{Alpha}\\d]+"));
    channels.add(new RegexpChannel(DOUBLE, "\\d++\\.\\d++"));
    channels.add(new RegexpChannel(INTEGER, "\\d++"));
    channels.add(new RegexpChannel(LITERAL, "\".*?\""));
    channels.add(new RegexpChannel(LITERAL, "'.*?'"));
    channels.add(new BlackHoleChannel("[\\s]"));
    channels.add(new RegexpChannel(PUNCTUATOR, "."));
    return new ChannelDispatcher<LexerOutput>(channels, true);
  }

}
