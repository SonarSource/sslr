/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
package org.sonar.sslr.test.channel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.sonar.sslr.channel.CodeReader;

public class ReaderHasNextCharMatcher extends BaseMatcher<CodeReader> {

  private final char nextChar;

  public ReaderHasNextCharMatcher(char nextChar) {
    this.nextChar = nextChar;
  }

  @Override
  public boolean matches(Object arg0) {
    if (!(arg0 instanceof CodeReader)) {
      return false;
    }
    CodeReader reader = (CodeReader) arg0;
    return reader.peek() == nextChar;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("next char is '" + nextChar + "'");
  }

}
