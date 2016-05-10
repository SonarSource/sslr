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
package org.sonar.sslr.text;

import org.sonar.sslr.internal.text.LocatedText;
import org.sonar.sslr.internal.text.PlainText;

import java.io.File;

/**
 * @since 1.17
 * @deprecated in 1.20, use your own text API instead.
 */
@Deprecated
public class Texts {

  private Texts() {
  }

  public static final char LF = '\n';
  public static final char CR = '\r';

  public static Text create(String str) {
    return new PlainText(str.toCharArray());
  }

  public static Text create(File fromFile, String fileContent) {
    return new LocatedText(fromFile, fileContent.toCharArray());
  }

  public static Text create(File fromFile, char[] fileContent) {
    return new LocatedText(fromFile, fileContent);
  }

}
