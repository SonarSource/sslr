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

import org.sonar.sslr.internal.text.TextBuilderImpl;
import org.sonar.sslr.internal.text.TextImpl;

import java.util.List;

/**
 * <p>This class is not intended to be sub-classed by clients.</p>
 *
 * @since 1.17
 */
public class PreprocessorsChain {

  private final List<Preprocessor> preprocessors;

  public PreprocessorsChain(List<Preprocessor> preprocessors) {
    this.preprocessors = preprocessors;
  }

  public TextBuilder process(Text input) {
    TextBuilder result = new TextBuilderImpl().append(input);
    for (Preprocessor preprocessor : preprocessors) {
      result = preprocessor.process(new PreprocessorContext(new TextImpl(result)));
    }
    return result;
  }

}
