/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.api.GenericTokenType.CONSTANT;
import static com.sonar.sslr.api.GenericTokenType.EOL;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;

import java.util.ArrayList;
import java.util.List;

import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;
import com.sonar.sslr.impl.channel.RegexpChannel;

public class DslLexer extends Lexer {

  @Override
  protected ChannelDispatcher<LexerOutput> getChannelDispatcher() {
    List<Channel> channels = new ArrayList<Channel>();
    channels.add(new RegexpChannel(IDENTIFIER, "\\p{Alpha}+"));
    channels.add(new RegexpChannel(CONSTANT, "\\d+"));
    channels.add(new PunctuatorChannel(DslPunctuator.values()));
    channels.add(new RegexpChannel(EOL, "\\r?\\n"));
    channels.add(new BlackHoleChannel("[\\s]"));
    return new ChannelDispatcher<LexerOutput>(channels, true);
  }

}
