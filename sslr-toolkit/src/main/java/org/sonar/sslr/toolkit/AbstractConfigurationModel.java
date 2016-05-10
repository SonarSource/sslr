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
package org.sonar.sslr.toolkit;

import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;

import java.nio.charset.Charset;
import java.util.List;

/**
 * This class provides an default optimized implementation of the {@link ConfigurationModel} interface.
 *
 * It will call the {@link #doGetParser()} and {@link #doGetTokenizers()} methods only when a change
 * to the configuration has been made.
 *
 * @since 1.17
 */
public abstract class AbstractConfigurationModel implements ConfigurationModel {

  private boolean updatedFlag;

  private Parser parser;
  private List<Tokenizer> tokenizers;

  public AbstractConfigurationModel() {
    this.updatedFlag = true;
  }

  @Override
  public void setUpdatedFlag() {
    updatedFlag = true;
  }

  private void ensureUpToDate() {
    if (updatedFlag) {
      parser = doGetParser();
      tokenizers = doGetTokenizers();
    }

    updatedFlag = false;
  }

  /**
   * Gets the charset reflecting the current configuration state.
   *
   * @return Charset for the current configuration
   */
  @Override
  public Charset getCharset() {
    return Charset.defaultCharset();
  }

  @Override
  public Parser getParser() {
    ensureUpToDate();
    return parser;
  }

  @Override
  public List<Tokenizer> getTokenizers() {
    ensureUpToDate();
    return tokenizers;
  }

  /**
   * Gets a parser instance reflecting the current configuration state.
   * This method will not be called twice in a row without a change in the configuration state.
   *
   * @return A parser for the current configuration
   */
  public abstract Parser doGetParser();

  /**
   * Gets tokenizers reflecting the current configuration state.
   * This method will not be called twice in a row without a change in the configuration state.
   *
   * @return Tokenizers for the current configuration
   */
  public abstract List<Tokenizer> doGetTokenizers();

}
