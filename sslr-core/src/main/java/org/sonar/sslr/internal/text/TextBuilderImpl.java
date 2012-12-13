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
package org.sonar.sslr.internal.text;

import com.google.common.collect.Lists;
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextBuilder;

import java.util.List;

/**
 * @since 1.17
 */
public class TextBuilderImpl implements TextBuilder {

  private final List<Text> fragments;

  public TextBuilderImpl() {
    this.fragments = Lists.newLinkedList();
  }

  public TextBuilderImpl append(Text text) {
    fragments.add(text);
    return this;
  }

  public List<Text> getFragments() {
    return fragments;
  }

}
