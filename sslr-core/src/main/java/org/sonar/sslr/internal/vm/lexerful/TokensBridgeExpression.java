/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.sslr.internal.vm.lexerful;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.NativeExpression;

public class TokensBridgeExpression extends NativeExpression implements Matcher {

  private final TokenType from;
  private final TokenType to;

  public TokensBridgeExpression(TokenType from, TokenType to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public void execute(Machine machine) {
    int length = machine.length();
    if (length < 2 || machine.tokenAt(0).getType() != from) {
      machine.backtrack();
      return;
    }
    int offset = 0;
    int bridgeLevel = 1;
    Token token;
    do {
      offset++;
      if (offset >= length) {
        machine.backtrack();
        return;
      }
      token = machine.tokenAt(offset);
      if (token.getType() == from) {
        bridgeLevel++;
      } else if (token.getType() == to) {
        bridgeLevel--;
      } else {
        // nop
      }
    } while (bridgeLevel != 0);
    for (int i = 0; i <= offset; i++) {
      machine.createLeafNode(this, 1);
    }
    machine.jump(1);
  }

  @Override
  public String toString() {
    return "Bridge[" + from + "," + to + "]";
  }

}
