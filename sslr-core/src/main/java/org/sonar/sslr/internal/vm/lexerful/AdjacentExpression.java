/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.internal.vm.lexerful;

import com.sonar.sslr.api.Token;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.NativeExpression;

public class AdjacentExpression extends NativeExpression {

  public static final AdjacentExpression INSTANCE = new AdjacentExpression();

  private AdjacentExpression() {
  }

  @Override
  public void execute(Machine machine) {
    if (machine.getIndex() == 0) {
      machine.backtrack();
      return;
    }
    Token previousToken = machine.tokenAt(-1);
    Token nextToken = machine.tokenAt(0);
    if (nextToken.getColumn() <= previousToken.getColumn() + previousToken.getValue().length()
        && nextToken.getLine() == previousToken.getLine()) {
      machine.jump(1);
    } else {
      machine.backtrack();
    }
  }

  @Override
  public String toString() {
    return "Adjacent";
  }

}
