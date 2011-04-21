/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import static com.sonar.sslr.api.GenericTokenType.COMMENT;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

import java.util.ArrayList;
import java.util.List;

import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;

public class IdentifierLexer extends Lexer {

	@Override
	protected ChannelDispatcher<LexerOutput> getChannelDispatcher() {
		List<Channel> channels = new ArrayList<Channel>();
		
		channels.add(regexp(COMMENT, "!COMMENT!"));
		channels.add(new IdentifierAndKeywordChannel("[a-zA-Z][a-zA-Z0-9]*", true)); /* Case sensitive */
		channels.add(new BlackHoleChannel("[ \t\r\n]+"));
		
		return new ChannelDispatcher<LexerOutput>(channels, true);
	}

}
