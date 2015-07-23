/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.vm.lexerful;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.NativeExpression;

public class TillNewLineExpression extends NativeExpression implements Matcher {

  public static final TillNewLineExpression INSTANCE = new TillNewLineExpression();

  private TillNewLineExpression() {
  }

  @Override
  public void execute(Machine machine) {
    int currentLine = machine.getIndex() == 0 ? 1 : machine.tokenAt(-1).getLine();
    int offset = 0;
    Token token = machine.tokenAt(offset);
    while (token.getLine() == currentLine && token.getType() != GenericTokenType.EOF) {
      offset++;
      token = machine.tokenAt(offset);
    }
    for (int i = 0; i < offset; i++) {
      machine.createLeafNode(this, 1);
    }
    machine.jump(1);
  }

  @Override
  public String toString() {
    return "TillNewLine";
  }

}
