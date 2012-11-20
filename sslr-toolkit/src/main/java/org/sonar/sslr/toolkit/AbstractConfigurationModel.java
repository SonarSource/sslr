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
package org.sonar.sslr.toolkit;


import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;

import java.util.List;

public abstract class AbstractConfigurationModel implements ConfigurationModel {

  private boolean updatedFlag;

  private Parser<? extends Grammar> parser;
  private List<Tokenizer> tokenizers;

  public AbstractConfigurationModel() {
    this.updatedFlag = true;
  }

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

  public Parser<? extends Grammar> getParser() {
    ensureUpToDate();
    return parser;
  }

  public List<Tokenizer> getTokenizers() {
    ensureUpToDate();
    return tokenizers;
  }

  public abstract List<ConfigurationProperty> getProperties();

  public abstract Parser<? extends Grammar> doGetParser();

  public abstract List<Tokenizer> doGetTokenizers();

}
