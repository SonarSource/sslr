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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.text.Position;
import org.sonar.sslr.text.TextCursor;
import org.sonar.sslr.text.TextLine;
import org.sonar.sslr.text.TextMarker;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_construct_generated_text() {
    assertThat(new TextImpl("").length()).isEqualTo(0);

    TextImpl text = new TextImpl("abc\r\nedf");

    assertThat(text.length()).isEqualTo(8);

    assertThat(text.charAt(0)).isEqualTo('a');
    assertThat(text.charAt(1)).isEqualTo('b');
    assertThat(text.charAt(2)).isEqualTo('c');
    assertThat(text.charAt(3)).isEqualTo('\r');
    assertThat(text.charAt(4)).isEqualTo('\n');
    assertThat(text.charAt(5)).isEqualTo('e');
    assertThat(text.charAt(6)).isEqualTo('d');
    assertThat(text.charAt(7)).isEqualTo('f');

    assertThat(text.getPosition(0)).isEqualTo(new Position(1, 1));
    assertThat(text.getPosition(1)).isEqualTo(new Position(1, 2));
    assertThat(text.getPosition(2)).isEqualTo(new Position(1, 3));
    assertThat(text.getPosition(3)).isEqualTo(new Position(1, 4));
    assertThat(text.getPosition(4)).isEqualTo(new Position(1, 5));
    assertThat(text.getPosition(5)).isEqualTo(new Position(2, 1));
    assertThat(text.getPosition(6)).isEqualTo(new Position(2, 2));
    assertThat(text.getPosition(7)).isEqualTo(new Position(2, 3));

    assertThat(text.getOriginalPosition(0)).isEqualTo(new Position(null, 1, 1));
    assertThat(text.getOriginalPosition(1)).isEqualTo(new Position(null, 1, 2));
    assertThat(text.getOriginalPosition(2)).isEqualTo(new Position(null, 1, 3));
    assertThat(text.getOriginalPosition(3)).isEqualTo(new Position(null, 1, 4));
    assertThat(text.getOriginalPosition(4)).isEqualTo(new Position(null, 1, 5));
    assertThat(text.getOriginalPosition(5)).isEqualTo(new Position(null, 2, 1));
    assertThat(text.getOriginalPosition(6)).isEqualTo(new Position(null, 2, 2));
    assertThat(text.getOriginalPosition(7)).isEqualTo(new Position(null, 2, 3));

    assertThat(text.getTextMarkers(0)).isEmpty();
    assertThat(text.getTextMarkers(1)).isEmpty();
    assertThat(text.getTextMarkers(2)).isEmpty();
    assertThat(text.getTextMarkers(3)).isEmpty();
    assertThat(text.getTextMarkers(4)).isEmpty();
    assertThat(text.getTextMarkers(5)).isEmpty();
    assertThat(text.getTextMarkers(6)).isEmpty();
    assertThat(text.getTextMarkers(7)).isEmpty();
  }

  @Test
  public void should_construct_from_file() {
    File originalFile = mock(File.class);

    assertThat(new TextImpl("".toCharArray(), originalFile).length()).isEqualTo(0);

    TextImpl text = new TextImpl("abc\r\nedf".toCharArray(), originalFile);

    assertThat(text.length()).isEqualTo(8);

    assertThat(text.charAt(0)).isEqualTo('a');
    assertThat(text.charAt(1)).isEqualTo('b');
    assertThat(text.charAt(2)).isEqualTo('c');
    assertThat(text.charAt(3)).isEqualTo('\r');
    assertThat(text.charAt(4)).isEqualTo('\n');
    assertThat(text.charAt(5)).isEqualTo('e');
    assertThat(text.charAt(6)).isEqualTo('d');
    assertThat(text.charAt(7)).isEqualTo('f');

    assertThat(text.getPosition(0)).isEqualTo(new Position(1, 1));
    assertThat(text.getPosition(1)).isEqualTo(new Position(1, 2));
    assertThat(text.getPosition(2)).isEqualTo(new Position(1, 3));
    assertThat(text.getPosition(3)).isEqualTo(new Position(1, 4));
    assertThat(text.getPosition(4)).isEqualTo(new Position(1, 5));
    assertThat(text.getPosition(5)).isEqualTo(new Position(2, 1));
    assertThat(text.getPosition(6)).isEqualTo(new Position(2, 2));
    assertThat(text.getPosition(7)).isEqualTo(new Position(2, 3));

    assertThat(text.getOriginalPosition(0)).isEqualTo(new Position(originalFile, 1, 1));
    assertThat(text.getOriginalPosition(1)).isEqualTo(new Position(originalFile, 1, 2));
    assertThat(text.getOriginalPosition(2)).isEqualTo(new Position(originalFile, 1, 3));
    assertThat(text.getOriginalPosition(3)).isEqualTo(new Position(originalFile, 1, 4));
    assertThat(text.getOriginalPosition(4)).isEqualTo(new Position(originalFile, 1, 5));
    assertThat(text.getOriginalPosition(5)).isEqualTo(new Position(originalFile, 2, 1));
    assertThat(text.getOriginalPosition(6)).isEqualTo(new Position(originalFile, 2, 2));
    assertThat(text.getOriginalPosition(7)).isEqualTo(new Position(originalFile, 2, 3));

    assertThat(text.getTextMarkers(0)).isEmpty();
    assertThat(text.getTextMarkers(1)).isEmpty();
    assertThat(text.getTextMarkers(2)).isEmpty();
    assertThat(text.getTextMarkers(3)).isEmpty();
    assertThat(text.getTextMarkers(4)).isEmpty();
    assertThat(text.getTextMarkers(5)).isEmpty();
    assertThat(text.getTextMarkers(6)).isEmpty();
    assertThat(text.getTextMarkers(7)).isEmpty();
  }

  @Test
  public void should_construct_from_text_builder() {
    assertThat(new TextImpl(new TextBuilderImpl()).length()).isEqualTo(0);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.append(new TextImpl("abc\r"));
    File originalFile = mock(File.class);
    textBuilder.append(new TextImpl("\ne".toCharArray(), originalFile));
    textBuilder.append(new TextImpl("df"));
    TextImpl text = new TextImpl(textBuilder);

    assertThat(text.length()).isEqualTo(8);

    assertThat(text.charAt(0)).isEqualTo('a');
    assertThat(text.charAt(1)).isEqualTo('b');
    assertThat(text.charAt(2)).isEqualTo('c');
    assertThat(text.charAt(3)).isEqualTo('\r');
    assertThat(text.charAt(4)).isEqualTo('\n');
    assertThat(text.charAt(5)).isEqualTo('e');
    assertThat(text.charAt(6)).isEqualTo('d');
    assertThat(text.charAt(7)).isEqualTo('f');

    assertThat(text.getPosition(0)).isEqualTo(new Position(1, 1));
    assertThat(text.getPosition(1)).isEqualTo(new Position(1, 2));
    assertThat(text.getPosition(2)).isEqualTo(new Position(1, 3));
    assertThat(text.getPosition(3)).isEqualTo(new Position(1, 4));
    assertThat(text.getPosition(4)).isEqualTo(new Position(1, 5));
    assertThat(text.getPosition(5)).isEqualTo(new Position(2, 1));
    assertThat(text.getPosition(6)).isEqualTo(new Position(2, 2));
    assertThat(text.getPosition(7)).isEqualTo(new Position(2, 3));

    assertThat(text.getOriginalPosition(0)).isEqualTo(new Position(null, 1, 1));
    assertThat(text.getOriginalPosition(1)).isEqualTo(new Position(null, 1, 2));
    assertThat(text.getOriginalPosition(2)).isEqualTo(new Position(null, 1, 3));
    assertThat(text.getOriginalPosition(3)).isEqualTo(new Position(null, 1, 4));
    assertThat(text.getOriginalPosition(4)).isEqualTo(new Position(originalFile, 1, 1));
    assertThat(text.getOriginalPosition(5)).isEqualTo(new Position(originalFile, 2, 1));
    assertThat(text.getOriginalPosition(6)).isEqualTo(new Position(null, 1, 1));
    assertThat(text.getOriginalPosition(7)).isEqualTo(new Position(null, 1, 2));

    assertThat(text.getTextMarkers(0)).isEmpty();
    assertThat(text.getTextMarkers(1)).isEmpty();
    assertThat(text.getTextMarkers(2)).isEmpty();
    assertThat(text.getTextMarkers(3)).isEmpty();
    assertThat(text.getTextMarkers(4)).isEmpty();
    assertThat(text.getTextMarkers(5)).isEmpty();
    assertThat(text.getTextMarkers(6)).isEmpty();
    assertThat(text.getTextMarkers(7)).isEmpty();
  }

  @Test
  public void should_construct_sub_sequence() {
    assertThat(new TextImpl("").subSequence(0, 0).length()).isEqualTo(0);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.append(new TextImpl("abc\r"));
    File originalFile = mock(File.class);
    textBuilder.append(new TextImpl("\ne".toCharArray(), originalFile));
    textBuilder.append(new TextImpl("df"));
    TextImpl text = new TextImpl(textBuilder);
    TextImpl subText = text.subSequence(2, 7);

    assertThat(subText.length()).isEqualTo(5);

    assertThat(subText.charAt(0)).isEqualTo('c');
    assertThat(subText.charAt(1)).isEqualTo('\r');
    assertThat(subText.charAt(2)).isEqualTo('\n');
    assertThat(subText.charAt(3)).isEqualTo('e');
    assertThat(subText.charAt(4)).isEqualTo('d');

    assertThat(subText.getPosition(0)).isEqualTo(new Position(1, 1));
    assertThat(subText.getPosition(1)).isEqualTo(new Position(1, 2));
    assertThat(subText.getPosition(2)).isEqualTo(new Position(1, 3));
    assertThat(subText.getPosition(3)).isEqualTo(new Position(2, 1));
    assertThat(subText.getPosition(4)).isEqualTo(new Position(2, 2));

    assertThat(subText.getOriginalPosition(0)).isEqualTo(new Position(null, 1, 3));
    assertThat(subText.getOriginalPosition(1)).isEqualTo(new Position(null, 1, 4));
    assertThat(subText.getOriginalPosition(2)).isEqualTo(new Position(originalFile, 1, 1));
    assertThat(subText.getOriginalPosition(3)).isEqualTo(new Position(originalFile, 2, 1));
    assertThat(subText.getOriginalPosition(4)).isEqualTo(new Position(null, 1, 1));

    assertThat(text.getTextMarkers(0)).isEmpty();
    assertThat(text.getTextMarkers(1)).isEmpty();
    assertThat(text.getTextMarkers(2)).isEmpty();
    assertThat(text.getTextMarkers(3)).isEmpty();
    assertThat(text.getTextMarkers(4)).isEmpty();
    assertThat(text.getTextMarkers(5)).isEmpty();
    assertThat(text.getTextMarkers(6)).isEmpty();
    assertThat(text.getTextMarkers(7)).isEmpty();
  }

  @Test
  public void should_not_sub_sequence_beyond_text_length() {
    thrown.expect(IndexOutOfBoundsException.class);
    new TextImpl("").subSequence(0, 1);
  }

  @Test
  public void should_not_sub_sequence_with_negative_index() {
    thrown.expect(IndexOutOfBoundsException.class);
    new TextImpl("").subSequence(-1, 0);
  }

  @Test
  public void should_not_sub_sequence_with_start_smaller_than_end() {
    thrown.expect(IndexOutOfBoundsException.class);
    new TextImpl("foo").subSequence(1, 0);
  }

  @Test
  public void cursor() {
    TextImpl text = new TextImpl("foo");
    TextCursor cursor = text.cursor();

    assertThat(cursor.length()).isEqualTo(3);
  }

  @Test
  public void lines() {
    Iterator<TextLine> it = new TextImpl("").lines().iterator();
    assertThat(it.hasNext()).isTrue();
    it.next();
    assertThat(it.hasNext()).isFalse();
  }

  @Test
  public void should_inject_text_marker() {
    TextMarker marker1 = mock(TextMarker.class);
    TextImpl fragment1 = new TextImpl("foo");

    TextMarker marker2 = mock(TextMarker.class);
    TextImpl fragment2 = new TextImpl("bar");

    TextBuilderImpl textBuilderImpl = mock(TextBuilderImpl.class);
    when(textBuilderImpl.getFragments()).thenReturn((List) Lists.newArrayList(fragment1, fragment2));
    when(textBuilderImpl.getTextEndMarkers(fragment1)).thenReturn(Lists.newArrayList(marker1));
    when(textBuilderImpl.getTextEndMarkers(fragment2)).thenReturn(Lists.newArrayList(marker2, marker1));

    TextImpl text = new TextImpl(textBuilderImpl);

    assertThat(text.getTextMarkers(0)).isEqualTo(Lists.newArrayList(marker1));
    assertThat(text.getTextMarkers(1)).isEqualTo(Lists.newArrayList(marker1));
    assertThat(text.getTextMarkers(2)).isEqualTo(Lists.newArrayList(marker1));
    assertThat(text.getTextMarkers(3)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(text.getTextMarkers(4)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(text.getTextMarkers(5)).isEqualTo(Lists.newArrayList(marker2, marker1));
  }

  @Test
  public void should_inject_text_several_markers() {
    TextMarker marker1 = mock(TextMarker.class);
    TextMarker marker2 = mock(TextMarker.class);
    TextImpl fragment = new TextImpl("foo");

    TextBuilderImpl textBuilderImpl = mock(TextBuilderImpl.class);
    when(textBuilderImpl.getFragments()).thenReturn((List) Lists.newArrayList(fragment));
    when(textBuilderImpl.getTextEndMarkers(fragment)).thenReturn(Lists.newArrayList(marker2, marker1));

    TextImpl text = new TextImpl(textBuilderImpl);

    assertThat(text.getTextMarkers(0)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(text.getTextMarkers(1)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(text.getTextMarkers(2)).isEqualTo(Lists.newArrayList(marker2, marker1));
  }

  @Test
  public void should_keep_old_markers_and_inject_new() {
    TextMarker marker1 = mock(TextMarker.class);
    TextMarker marker2 = mock(TextMarker.class);
    TextImpl fragment1 = new TextImpl("foo");

    TextBuilderImpl textBuilderImpl = mock(TextBuilderImpl.class);
    when(textBuilderImpl.getFragments()).thenReturn((List) Lists.newArrayList(fragment1));
    when(textBuilderImpl.getTextEndMarkers(fragment1)).thenReturn(Lists.newArrayList(marker2, marker1));

    TextImpl fragment2 = new TextImpl(textBuilderImpl);

    assertThat(fragment2.getTextMarkers(0)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(fragment2.getTextMarkers(1)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(fragment2.getTextMarkers(2)).isEqualTo(Lists.newArrayList(marker2, marker1));

    TextMarker marker3 = mock(TextMarker.class);
    TextMarker marker4 = mock(TextMarker.class);

    textBuilderImpl = mock(TextBuilderImpl.class);
    when(textBuilderImpl.getFragments()).thenReturn((List) Lists.newArrayList(fragment2));
    when(textBuilderImpl.getTextEndMarkers(fragment2)).thenReturn(Lists.newArrayList(marker4, marker3));

    TextImpl text = new TextImpl(textBuilderImpl);

    assertThat(text.getTextMarkers(0)).isEqualTo(Lists.newArrayList(marker4, marker3, marker2, marker1));
    assertThat(text.getTextMarkers(1)).isEqualTo(Lists.newArrayList(marker4, marker3, marker2, marker1));
    assertThat(text.getTextMarkers(2)).isEqualTo(Lists.newArrayList(marker4, marker3, marker2, marker1));
  }

  @Test
  public void should_preserve_text_markers_on_sub_sequence() {
    TextMarker marker1 = mock(TextMarker.class);
    TextImpl fragment1 = new TextImpl("foo");

    TextMarker marker2 = mock(TextMarker.class);
    TextImpl fragment2 = new TextImpl("bar");

    TextBuilderImpl textBuilderImpl = mock(TextBuilderImpl.class);
    when(textBuilderImpl.getFragments()).thenReturn((List) Lists.newArrayList(fragment1, fragment2));
    when(textBuilderImpl.getTextEndMarkers(fragment1)).thenReturn(Lists.newArrayList(marker1));
    when(textBuilderImpl.getTextEndMarkers(fragment2)).thenReturn(Lists.newArrayList(marker2, marker1));

    TextImpl text = new TextImpl(textBuilderImpl);

    assertThat(text.getTextMarkers(0)).isEqualTo(Lists.newArrayList(marker1));
    assertThat(text.getTextMarkers(1)).isEqualTo(Lists.newArrayList(marker1));
    assertThat(text.getTextMarkers(2)).isEqualTo(Lists.newArrayList(marker1));
    assertThat(text.getTextMarkers(3)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(text.getTextMarkers(4)).isEqualTo(Lists.newArrayList(marker2, marker1));
    assertThat(text.getTextMarkers(5)).isEqualTo(Lists.newArrayList(marker2, marker1));

    TextImpl subText = text.subSequence(2, 4);
    assertThat(subText.getTextMarkers(0)).isEqualTo(Lists.newArrayList(marker1));
    assertThat(subText.getTextMarkers(1)).isEqualTo(Lists.newArrayList(marker2, marker1));
  }

}
