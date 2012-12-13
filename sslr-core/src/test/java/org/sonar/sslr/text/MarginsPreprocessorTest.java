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

import org.junit.Test;
import org.sonar.sslr.internal.text.TextImpl;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;

public class MarginsPreprocessorTest {

  @Test
  public void test() {
    Preprocessor preprocessor = new MarginsPreprocessor(1, 5);
    PreprocessorsChain chain = new PreprocessorsChain(Collections.singletonList(preprocessor));

    StringBuilder input = new StringBuilder();
    input.append(" foo  \r\n");
    input.append(" bar  \n");
    input.append(" baz\n");
    input.append("\r");
    input.append(" qux");

    TextBuilder result = chain.process(new TextImpl(input.toString()));

    String expected = "foo \r\nbar \nbaz\n\rqux";

    assertThat(new TextImpl(result).length()).isEqualTo(expected.length());
    assertThat(new TextImpl(result).startsWith(expected)).isTrue();
  }

  private static class MarginsPreprocessor implements Preprocessor {

    private final int leftMargin;
    private final int rightMargin;

    public MarginsPreprocessor(int leftMargin, int rightMargin) {
      this.leftMargin = leftMargin;
      this.rightMargin = rightMargin;
    }

    public TextBuilder process(PreprocessorContext context) {
      TextBuilder result = context.createEmptyTextBuilder();

      for (TextLine line : context.getInput().lines()) {
        result.append(line.subSequence(Math.min(leftMargin, line.length()), Math.min(rightMargin, line.length())));
        result.append(line.getLineTerminator());
      }

      return result;
    }

  }

}
