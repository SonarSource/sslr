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
package com.sonar.sslr.test.minic;

import com.google.common.collect.ImmutableList;
import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.toolkit.AbstractConfigurationModel;
import org.sonar.sslr.toolkit.ConfigurationProperty;
import org.sonar.sslr.toolkit.Toolkit;
import org.sonar.sslr.toolkit.ValidationCallback;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

public final class MiniCToolkit {

  private MiniCToolkit() {
  }

  public static void main(String[] args) {
    Toolkit toolkit = new Toolkit("SonarSource : MiniC : Toolkit", new MiniCConfigurationModel());
    toolkit.run();
  }

  static class MiniCConfigurationModel extends AbstractConfigurationModel {

    private final ConfigurationProperty charsetProperty = new ConfigurationProperty("Charset", "Charset used when opening files.", "UTF-8", new ValidationCallback() {

      @Override
      public String validate(String newValueCandidate) {
        try {
          Charset.forName(newValueCandidate);
          return "";
        } catch (IllegalCharsetNameException e) {
          return "Illegal charset name: " + newValueCandidate;
        } catch (UnsupportedCharsetException e) {
          return "Unsupported charset: " + newValueCandidate;
        }
      }

    });

    @Override
    public List<ConfigurationProperty> getProperties() {
      return ImmutableList.of(charsetProperty);
    }

    @Override
    public Charset getCharset() {
      return Charset.forName(charsetProperty.getValue());
    }

    @Override
    public Parser doGetParser() {
      updateConfiguration();
      return MiniCParser.create();
    }

    @Override
    public List<Tokenizer> doGetTokenizers() {
      updateConfiguration();
      return MiniCColorizer.getTokenizers();
    }

    private static void updateConfiguration() {
      /* Construct a parser configuration object from the properties */
    }

  }

}
