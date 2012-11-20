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
package com.sonar.sslr.test.miniC;

import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.toolkit.ConfigurationCallback;
import org.sonar.sslr.toolkit.ConfigurationProperty;
import org.sonar.sslr.toolkit.Toolkit;
import org.sonar.sslr.toolkit.ValidationCallback;

import java.util.List;

public final class MiniCToolkit {

  private MiniCToolkit() {
  }

  public static void main(String[] args) {
    ConfigurationCallback configurationCallback = new ConfigurationCallback() {

      public List<Tokenizer> getTokenizers(List<ConfigurationProperty> configurationProperties) {
        return MiniCColorizer.getTokenizers();
      }

      public Parser<? extends Grammar> getParser(List<ConfigurationProperty> configurationProperties) {
        return MiniCParser.create();
      }
    };

    List<ConfigurationProperty> configurationProperties = ImmutableList.of(
        new ConfigurationProperty("foo", "bar", "def foo"),
        new ConfigurationProperty("toto", "huuuhuuu", ""),
        new ConfigurationProperty("hmm", "hoho", "", new ValidationCallback() {

          public String validate(String newValueCandidate) {
            return newValueCandidate.length() <= 3 ? "" : "Length (" + newValueCandidate.length() + ") too long, max allowed is 3.";
          }

        }));

    Toolkit toolkit = new Toolkit("SonarSource : MiniC : Toolkit", configurationProperties, configurationCallback);
    toolkit.run();
  }

}
