/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.text;

import java.util.List;

/**
 * <p>This class is not intended to be subclassed by clients.</p>
 *
 * @since 1.17
 * @deprecated in 1.20, use your own text API instead.
 */
@Deprecated
public class PreprocessorsChain {

  private final List<Preprocessor> preprocessors;

  public PreprocessorsChain(List<Preprocessor> preprocessors) {
    this.preprocessors = preprocessors;
  }

  public Text process(Text input) {
    for (Preprocessor preprocessor : preprocessors) {
      input = preprocessor.process(new PreprocessorContext(input));
    }
    return input;
  }

}
