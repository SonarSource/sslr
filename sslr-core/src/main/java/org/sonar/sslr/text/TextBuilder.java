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

import org.sonar.sslr.internal.text.AbstractText;
import org.sonar.sslr.internal.text.CompositeText;
import org.sonar.sslr.internal.text.PlainText;
import org.sonar.sslr.internal.text.TransformedText;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class is not intended to be subclassed by clients.</p>
 *
 * @since 1.17
 * @deprecated in 1.20, use your own text API instead.
 */
@Deprecated
public class TextBuilder {

  private static final Text EMPTY = new PlainText(new char[0]);

  private final List<AbstractText> texts = new ArrayList<>();

  public static TextBuilder create() {
    return new TextBuilder();
  }

  private TextBuilder() {
  }

  public TextBuilder append(Text text) {
    if (text.length() != 0) {
      texts.add(cast(text));
    }
    return this;
  }

  public TextBuilder appendTransformation(Text from, Text to) {
    return append(new TransformedText(cast(from), cast(to)));
  }

  private static AbstractText cast(Text text) {
    // This cast is safe, even if not checked - AbstractText is a base implementation of interface Text
    return (AbstractText) text;
  }

  public Text build() {
    if (texts.isEmpty()) {
      return EMPTY;
    } else if (texts.size() == 1) {
      return texts.get(0);
    }
    return new CompositeText(texts);
  }

}
