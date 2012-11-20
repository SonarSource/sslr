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
package org.sonar.sslr.internal.toolkit;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.toolkit.ConfigurationCallback;
import org.sonar.sslr.toolkit.ConfigurationProperty;

import java.util.List;

public class ConfigurationModel {

  private boolean updatedFlag;
  private final List<ConfigurationProperty> configurationProperties;
  private final ConfigurationCallback configurationCallback;

  private Parser<? extends Grammar> parser;
  private List<Tokenizer> tokenizers;

  public ConfigurationModel(List<ConfigurationProperty> configurationProperties, ConfigurationCallback configurationCallback) {
    Preconditions.checkNotNull(configurationProperties);
    Preconditions.checkNotNull(configurationCallback);

    this.updatedFlag = true;
    this.configurationProperties = configurationProperties;
    this.configurationCallback = configurationCallback;
  }

  public void setUpdatedFlag() {
    updatedFlag = true;
  }

  private void ensureUpToDate() {
    if (updatedFlag) {
      parser = configurationCallback.getParser(configurationProperties);
      tokenizers = configurationCallback.getTokenizers(configurationProperties);
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

  public List<ConfigurationProperty> getProperties() {
    return configurationProperties;
  }

}
