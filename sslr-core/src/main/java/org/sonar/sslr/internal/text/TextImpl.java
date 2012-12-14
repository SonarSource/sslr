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
import com.google.common.collect.ImmutableList;
import org.sonar.sslr.text.Position;
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextBuilder;
import org.sonar.sslr.text.TextCursor;
import org.sonar.sslr.text.TextLine;
import org.sonar.sslr.text.TextMarker;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @since 1.17
 */
public class TextImpl extends AbstractTextOperations implements Text {

  private final char[] buffer;
  private final Position[] positions;
  private final Position[] originalPositions;
  private final int[] textMarkersOffsets;
  private final TextMarker[][] textMarkers;

  /**
   * Used for creating generated texts.
   */
  public TextImpl(String string) {
    this(string.toCharArray(), null);
  }

  /**
   * Used for creating file texts.
   */
  public TextImpl(char[] buffer, File originalFile) {
    this.buffer = new char[buffer.length];
    System.arraycopy(buffer, 0, this.buffer, 0, buffer.length);

    this.positions = TextUtils.getPositions(this.buffer);
    this.originalPositions = TextUtils.getPositionsWithFile(this.positions, originalFile);
    this.textMarkersOffsets = new int[] {0};
    this.textMarkers = new TextMarker[][] {new TextMarker[0]};
  }

  /**
   * Used for converting TextBuilder instances into TextImpl ones.
   */
  public TextImpl(TextBuilder textBuilder) {
    TextBuilderImpl textBuilderImpl = (TextBuilderImpl) textBuilder;

    int totalLength = 0;
    int totalNumberOfTextMarkerOffsets = 0;
    for (Text fragment : textBuilderImpl.getFragments()) {
      TextImpl fragmentImpl = (TextImpl) fragment;

      totalLength += fragmentImpl.length();
      totalNumberOfTextMarkerOffsets += fragmentImpl.textMarkersOffsets.length;
    }

    this.buffer = new char[totalLength];
    this.originalPositions = new Position[totalLength];
    this.textMarkersOffsets = new int[totalNumberOfTextMarkerOffsets];
    this.textMarkers = new TextMarker[totalNumberOfTextMarkerOffsets][];

    int currentLength = 0;
    int currentTextMarkerIndex = 0;
    for (Text fragment : textBuilderImpl.getFragments()) {
      TextImpl fragmentImpl = (TextImpl) fragment;

      System.arraycopy(fragmentImpl.buffer, 0, this.buffer, currentLength, fragmentImpl.length());
      System.arraycopy(fragmentImpl.originalPositions, 0, this.originalPositions, currentLength, fragmentImpl.length());

      List<TextMarker> newTextMarkersList = textBuilderImpl.getTextEndMarkers(fragmentImpl);
      TextMarker[] newTextMarkers = newTextMarkersList.toArray(new TextMarker[newTextMarkersList.size()]);

      for (int i = 0; i < fragmentImpl.textMarkersOffsets.length; i++) {
        this.textMarkersOffsets[currentTextMarkerIndex] = currentLength + fragmentImpl.textMarkersOffsets[i];

        TextMarker[] existingTextMarkers = fragmentImpl.textMarkers[i];
        this.textMarkers[currentTextMarkerIndex] = new TextMarker[newTextMarkers.length + existingTextMarkers.length];
        System.arraycopy(newTextMarkers, 0, this.textMarkers[currentTextMarkerIndex], 0, newTextMarkers.length);
        System.arraycopy(existingTextMarkers, 0, this.textMarkers[currentTextMarkerIndex], newTextMarkers.length, existingTextMarkers.length);

        currentTextMarkerIndex++;
      }

      currentLength += fragment.length();
    }

    this.positions = TextUtils.getPositions(this.buffer);
  }

  /**
   * Used for creating subsequences.
   */
  private TextImpl(TextImpl text, int start, int end) {
    Preconditions.checkPositionIndexes(start, end, text.length());

    int newLength = end - start;

    this.buffer = new char[newLength];
    this.originalPositions = new Position[newLength];
    System.arraycopy(text.buffer, start, this.buffer, 0, newLength);
    System.arraycopy(text.originalPositions, start, this.originalPositions, 0, newLength);

    this.positions = TextUtils.getPositions(this.buffer);

    int firstTextMarkerIndex = text.getTextMarkerIndex(start);
    int lastTextMarkerIndex = text.getTextMarkerIndex(end);
    int textMarkersLength = lastTextMarkerIndex - firstTextMarkerIndex + 1;

    this.textMarkersOffsets = new int[textMarkersLength];
    this.textMarkers = new TextMarker[textMarkersLength][];
    for (int i = firstTextMarkerIndex; i <= lastTextMarkerIndex; i++) {
      int previousOffset = text.textMarkersOffsets[i];
      int newOffset = Math.max(0, previousOffset - start);
      int newI = i - firstTextMarkerIndex;

      this.textMarkersOffsets[newI] = newOffset;
      this.textMarkers[newI] = new TextMarker[text.textMarkers[i].length];
      System.arraycopy(text.textMarkers[i], 0, this.textMarkers[newI], 0, text.textMarkers[i].length);
    }
  }

  public int length() {
    return buffer.length;
  }

  public char charAt(int index) {
    return buffer[index];
  }

  public TextImpl subSequence(int start, int end) {
    return new TextImpl(this, start, end);
  }

  public Position getPosition(int index) {
    return positions[index];
  }

  public Position getOriginalPosition(int index) {
    return originalPositions[index];
  }

  public TextCursor cursor() {
    return new TextCursorImpl(this);
  }

  public Iterable<TextLine> lines() {
    final Text text = this;

    return new Iterable<TextLine>() {

      public Iterator<TextLine> iterator() {
        return new TextLineCursorImpl(text);
      }
    };
  }

  public List<TextMarker> getTextMarkers(int index) {
    return ImmutableList.copyOf(textMarkers[getTextMarkerIndex(index)]);
  }

  private int getTextMarkerIndex(int index) {
    int textMarkerIndex = Arrays.binarySearch(textMarkersOffsets, index);
    if (textMarkerIndex < 0) {
      int insertionPoint = -textMarkerIndex - 1;
      textMarkerIndex = insertionPoint - 1;
    }

    return textMarkerIndex;
  }

}
