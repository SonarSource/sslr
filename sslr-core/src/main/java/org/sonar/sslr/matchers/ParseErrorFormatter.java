/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
package org.sonar.sslr.matchers;

import org.sonar.sslr.internal.matchers.InputBuffer;

public class ParseErrorFormatter {

  public String format(ParseError parseError) {
    InputBuffer inputBuffer = parseError.getInputBuffer();
    InputBuffer.Position position = inputBuffer.getPosition(parseError.getErrorIndex());

    StringBuilder sb = new StringBuilder();
    sb.append("At line ").append(position.getLine())
        .append(" column ").append(position.getColumn())
        .append(' ').append(parseError.getMessage()).append('\n');
    sb.append(inputBuffer.extractLine(position.getLine()));
    for (int i = 1; i < position.getColumn(); i++) {
      sb.append(' ');
    }
    sb.append("^\n");

    return sb.toString();
  }

}
