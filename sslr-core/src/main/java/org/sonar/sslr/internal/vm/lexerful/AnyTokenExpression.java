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
package org.sonar.sslr.internal.vm.lexerful;

import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.NativeExpression;

public class AnyTokenExpression extends NativeExpression implements Matcher {

  public static final AnyTokenExpression INSTANCE = new AnyTokenExpression();

  private AnyTokenExpression() {
  }

  @Override
  public void execute(Machine machine) {
    if (machine.length() == 0) {
      machine.backtrack();
    } else {
      machine.createLeafNode(this, 1);
      machine.jump(1);
    }
  }

  @Override
  public String toString() {
    return "AnyToken";
  }

}
