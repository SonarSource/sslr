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
package org.sonar.sslr.text;

import com.google.common.collect.Lists;
import org.sonar.sslr.internal.text.AbstractText;
import org.sonar.sslr.internal.text.CompositeText;
import org.sonar.sslr.internal.text.TransformedText;

import java.util.List;

/**
 * <p>This class is not intended to be sub-classed by clients.</p>
 *
 * @since 1.17
 */
public class TextBuilder {

  private final List<AbstractText> texts = Lists.newArrayList();

  public static TextBuilder create() {
    return new TextBuilder();
  }

  private TextBuilder() {
  }

  public TextBuilder append(Text text) {
    texts.add(cast(text));
    return this;
  }

  public TextBuilder appendTransformation(Text from, Text to) {
    return append(new TransformedText(cast(from), cast(to)));
  }

  private AbstractText cast(Text text) {
    // This cast is safe, even if not checked - AbstractText is a base implementation of interface Text
    return (AbstractText) text;
  }

  public Text build() {
    if (texts.size() == 1) {
      return texts.get(0);
    }
    return new CompositeText(texts);
  }

}
