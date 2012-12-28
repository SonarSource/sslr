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

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreprocessorsChainTest {

  @Test
  public void test() {
    Preprocessor preprocessor1 = mock(Preprocessor.class);
    Preprocessor preprocessor2 = mock(Preprocessor.class);
    PreprocessorsChain preprocessorsChain = new PreprocessorsChain(ImmutableList.of(preprocessor1, preprocessor2));
    Text input = mock(Text.class);
    Text result1 = mock(Text.class);
    ArgumentCaptor<PreprocessorContext> preprocessorContext1 = ArgumentCaptor.forClass(PreprocessorContext.class);
    when(preprocessor1.process(preprocessorContext1.capture())).thenReturn(result1);
    Text result2 = mock(Text.class);
    ArgumentCaptor<PreprocessorContext> preprocessorContext2 = ArgumentCaptor.forClass(PreprocessorContext.class);
    when(preprocessor2.process(preprocessorContext2.capture())).thenReturn(result2);

    Text result = preprocessorsChain.process(input);
    assertThat(preprocessorContext1.getValue().getInput()).isSameAs(input);
    assertThat(preprocessorContext2.getValue().getInput()).isSameAs(result1);
    assertThat(result).isSameAs(result2);
  }

}
