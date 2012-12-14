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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextBuilder;
import org.sonar.sslr.text.TextMarker;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @since 1.17
 */
public class TextBuilderImpl implements TextBuilder {

  private final List<Text> fragments;
  private final Stack<TextMarker> pendingTextStartMarkers;
  private final Map<Text, Set<TextMarker>> textStartMarkersByText;
  private final LinkedListMultimap<Text, TextMarker> textMarkersByText;

  public TextBuilderImpl() {
    this.fragments = Lists.newLinkedList();
    this.pendingTextStartMarkers = new Stack<TextMarker>();
    this.textStartMarkersByText = Maps.newHashMap();
    this.textMarkersByText = LinkedListMultimap.create();
  }

  public TextBuilderImpl append(Text fragment) {
    fragments.add(fragment);
    textStartMarkersByText.put(fragment, ImmutableSet.copyOf(pendingTextStartMarkers));
    return this;
  }

  public TextBuilderImpl append(TextBuilder textBuilder) {
    for (Text fragment : ((TextBuilderImpl) textBuilder).getFragments()) {
      append(fragment);
    }
    return this;
  }

  public List<Text> getFragments() {
    return fragments;
  }

  public TextBuilderImpl appendStartMarker(TextMarker textMarker) {
    pendingTextStartMarkers.push(textMarker);
    return this;
  }

  public TextBuilderImpl appendEndMarker(TextMarker textMarker) {
    Preconditions.checkState(!pendingTextStartMarkers.isEmpty(), "Cannot append end markers before start ones");
    Preconditions.checkArgument(pendingTextStartMarkers.peek().equals(textMarker), "The end marker must match the last started one");

    pendingTextStartMarkers.pop();

    for (Text fragment : getFragments()) {
      if (textStartMarkersByText.get(fragment).contains(textMarker)) {
        textMarkersByText.put(fragment, textMarker);
      }
    }

    return this;
  }

  public List<TextMarker> getTextEndMarkers(Text fragment) {
    return textMarkersByText.get(fragment);
  }

}
