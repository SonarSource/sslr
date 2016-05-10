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
package org.sonar.sslr.internal.vm;

public class EndOfInputExpression extends NativeExpression {

  public static final EndOfInputExpression INSTANCE = new EndOfInputExpression();

  private EndOfInputExpression() {
  }

  @Override
  public void execute(Machine machine) {
    if (machine.length() == 0) {
      machine.jump(1);
    } else {
      machine.backtrack();
    }
  }

  @Override
  public String toString() {
    return "EndOfInput";
  }

}
