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

public class ReplacePreprocessorTest {

  @Test
  public void test() {
    Preprocessor preprocessor = new ReplacePreprocessor();
    PreprocessorsChain chain = new PreprocessorsChain(Collections.singletonList(preprocessor));

    Text input = new TextImpl("foobaz");

    TextBuilder result = chain.process(input);

    String expected = "barbaz";

    assertThat(new TextImpl(result).length()).isEqualTo(expected.length());
    assertThat(new TextImpl(result).startsWith(expected)).isTrue();
  }

  private static class ReplacePreprocessor implements Preprocessor {

    private final String toReplace = "foo";
    private final String replacement = "bar";

    public TextBuilder process(PreprocessorContext context) {
      TextBuilder result = context.createEmptyTextBuilder();
      TextCursor cursor = context.getInput().cursor();

      while (!cursor.isEmpty()) {
        if (cursor.startsWith(toReplace)) {
          result.append(context.createGeneratedTextFrom(replacement));
          cursor.moveForward(replacement.length());
        } else {
          result.append(cursor.subSequence(0, 1));
          cursor.moveForward(1);
        }
      }

      return result;
    }

  }

}
