/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.channel;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.sonar.sslr.impl.Lexer;

/**
 * Ignores all BOM characters.
 *
 * @since 1.17
 */
public class BomCharacterChannel extends Channel<Lexer> {

  public static final int BOM_CHAR = '\uFEFF';

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    if (code.peek() == BOM_CHAR) {
      code.pop();
      return true;
    }
    return false;
  }

}
