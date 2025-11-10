/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.test.minic.integration;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.minic.MiniCParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

public class MiniCOwnExamplesTest {

  private static final Parser<Grammar> parser = MiniCParser.create();

  @Test
  public void test() throws Exception {
    Collection<File> files = FileUtils.listFiles(new File("src/test/resources/MiniCIntegration"), null, true);
    assertThat(files).isNotEmpty();
    for (File file : files) {
      try {
        parser.parse(file);
      } catch (RuntimeException e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

}
