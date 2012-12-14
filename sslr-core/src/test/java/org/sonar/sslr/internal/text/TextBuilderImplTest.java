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
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextMarker;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextBuilderImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_start_with_empty_fragments() {
    assertThat(new TextBuilderImpl().getFragments()).isEmpty();
  }

  @Test
  public void should_append_texts() {
    Text t1 = mock(Text.class);
    Text t2 = mock(Text.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();

    textBuilder.append(t1);
    assertThat(textBuilder.getFragments()).containsExactly(t1);

    textBuilder.append(t2);
    assertThat(textBuilder.getFragments()).containsExactly(t1, t2);
  }

  @Test
  public void should_return_same_instance_for_chaining_of_append_text() {
    TextBuilderImpl textBuilder = new TextBuilderImpl();

    assertThat(textBuilder.append(mock(Text.class))).isSameAs(textBuilder);
  }

  @Test
  public void should_append_text_builders() {
    TextBuilderImpl appendedTextBuilder = mock(TextBuilderImpl.class);
    Text t1 = mock(Text.class);
    Text t2 = mock(Text.class);
    when(appendedTextBuilder.getFragments()).thenReturn(Lists.newArrayList(t1, t2));

    TextBuilderImpl textBuilder = new TextBuilderImpl();

    textBuilder.append(appendedTextBuilder);
    assertThat(textBuilder.getFragments()).containsExactly(t1, t2);
  }

  @Test
  public void should_return_same_instance_for_chaning_of_append_text_builders() {
    TextBuilderImpl textBuilder = new TextBuilderImpl();

    assertThat(textBuilder.append(mock(TextBuilderImpl.class))).isSameAs(textBuilder);
  }

  @Test
  public void should_not_fail_to_append_start_immedialty_followed_by_end() {
    TextMarker marker = mock(TextMarker.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.appendStartMarker(marker);
    textBuilder.appendEndMarker(marker);
  }

  @Test
  public void should_add_marker_to_text() {
    TextMarker marker1 = mock(TextMarker.class);
    Text text1 = mock(Text.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.appendStartMarker(marker1);
    textBuilder.append(text1);
    assertThat(textBuilder.getTextEndMarkers(text1)).isEmpty();
    textBuilder.appendEndMarker(marker1);
    assertThat(textBuilder.getTextEndMarkers(text1)).containsExactly(marker1);
  }

  @Test
  public void should_add_several_markers_to_text() {
    TextMarker marker1 = mock(TextMarker.class);
    TextMarker marker2 = mock(TextMarker.class);
    Text text1 = mock(Text.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.appendStartMarker(marker1);
    textBuilder.appendStartMarker(marker2);
    textBuilder.append(text1);
    assertThat(textBuilder.getTextEndMarkers(text1)).isEmpty();
    textBuilder.appendEndMarker(marker2);
    assertThat(textBuilder.getTextEndMarkers(text1)).containsExactly(marker2);
    textBuilder.appendEndMarker(marker1);
    assertThat(textBuilder.getTextEndMarkers(text1)).containsExactly(marker2, marker1);
  }

  @Test
  public void should_add_marker_to_several_texts() {
    TextMarker marker1 = mock(TextMarker.class);
    Text text1 = mock(Text.class);
    Text text2 = mock(Text.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.appendStartMarker(marker1);
    textBuilder.append(text1);
    textBuilder.append(text2);
    assertThat(textBuilder.getTextEndMarkers(text1)).isEmpty();
    assertThat(textBuilder.getTextEndMarkers(text2)).isEmpty();
    textBuilder.appendEndMarker(marker1);
    assertThat(textBuilder.getTextEndMarkers(text1)).containsExactly(marker1);
    assertThat(textBuilder.getTextEndMarkers(text2)).containsExactly(marker1);
  }

  @Test
  public void should_not_add_marker_when_start_and_end_occurs_before() {
    TextMarker marker1 = mock(TextMarker.class);
    Text text1 = mock(Text.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.appendStartMarker(marker1);
    textBuilder.appendEndMarker(marker1);
    textBuilder.append(text1);
    assertThat(textBuilder.getTextEndMarkers(text1)).isEmpty();
  }

  @Test
  public void should_not_add_marker_when_start_and_end_occurs_after() {
    TextMarker marker1 = mock(TextMarker.class);
    Text text1 = mock(Text.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.append(text1);
    textBuilder.appendStartMarker(marker1);
    textBuilder.appendEndMarker(marker1);
    assertThat(textBuilder.getTextEndMarkers(text1)).isEmpty();
  }

  @Test
  public void should_fail_to_end_when_no_marker_added_yet() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Cannot append end markers before start ones");
    new TextBuilderImpl().appendEndMarker(mock(TextMarker.class));
  }

  @Test
  public void should_fail_to_end_a_marker_different_than_the_last_added_one() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The end marker must match the last started one");

    TextMarker marker1 = mock(TextMarker.class);
    TextMarker marker2 = mock(TextMarker.class);

    TextBuilderImpl textBuilder = new TextBuilderImpl();
    textBuilder.appendStartMarker(marker1);
    textBuilder.appendEndMarker(marker2);
  }

}
