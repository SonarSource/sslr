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
package org.sonar.sslr.internal.vm.lexerful;

import com.sonar.sslr.api.Token;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.NativeExpression;

public class TokenValueExpression extends NativeExpression implements Matcher {

  private final String value;

  public TokenValueExpression(String value) {
    this.value = value;
  }

  @Override
  public void execute(Machine machine) {
    if (machine.length() == 0) {
      machine.backtrack();
      return;
    }
    Token token = machine.tokenAt(0);
    if (value.hashCode() == token.getValue().hashCode() && value.equals(token.getValue())) {
      machine.createLeafNode(this, 1);
      machine.jump(1);
    } else {
      machine.backtrack();
    }
  }

  @Override
  public String toString() {
    return "TokenValue " + value;
  }

}
