/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
/**
 * Provides a basic framework to sequentially read any kind of character stream in order to feed a generic OUTPUT.
 *
 * This framework can used for instance in order to :
 * <ul>
 *   <li>Create a lexer in charge to generate a list of tokens from a character stream</li>
 *   <li>Create a source code syntax highligther in charge to decorate a source code with HTML tags</li>
 *   <li>Create a javadoc generator</li>
 *   <li>...</li>
 * </ul>
 *
 * The entry point of this framework is the {@link org.sonar.sslr.channel.ChannelDispatcher} class.
 * This class must be initialized with a {@link org.sonar.sslr.channel.CodeReader} and a list of {@link org.sonar.sslr.channel.Channel}.
 *
 * The {@link org.sonar.sslr.channel.CodeReader} encapsulates any character stream in order to provide all mechanisms to Channels
 * in order to look ahead and look behind the current reading cursor position.
 *
 * A {@link org.sonar.sslr.channel.Channel} is in charge to consume the character stream through the CodeReader in order to feed
 * the OUTPUT.
 *
 * @since 1.20
 */
package org.sonar.sslr.channel;

