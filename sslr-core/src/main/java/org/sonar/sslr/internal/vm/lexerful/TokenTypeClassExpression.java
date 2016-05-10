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

import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.NativeExpression;

public class TokenTypeClassExpression extends NativeExpression implements Matcher {

  private final Class typeClass;

  public TokenTypeClassExpression(Class typeClass) {
    this.typeClass = typeClass;
  }

  @Override
  public void execute(Machine machine) {
    if (machine.length() == 0 || typeClass != machine.tokenAt(0).getType().getClass()) {
      machine.backtrack();
      return;
    }
    machine.createLeafNode(this, 1);
    machine.jump(1);
  }

  @Override
  public String toString() {
    return "TokenTypeClass " + typeClass;
  }

}
