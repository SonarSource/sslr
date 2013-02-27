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
package com.sonar.sslr.test.miniC.integration;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import com.sonar.sslr.impl.events.ExtendedStackTraceStream;
import com.sonar.sslr.test.miniC.MiniCParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class MiniCOwnExamplesTest {

  private static ExtendedStackTrace extendedStackTrace = new ExtendedStackTrace();

  private File file = null;
  private static final Parser<Grammar> parser = MiniCParser.create();
  private static final Parser<Grammar> parserDebug = MiniCParser.create(extendedStackTrace);

  @Parameterized.Parameters
  public static Collection<Object[]> getFiles() throws URISyntaxException {
    return getParameters("MiniCIntegration");
  }

  public MiniCOwnExamplesTest(File f) {
    this.file = f;
  }

  @Test
  public void parseSources() throws IOException, URISyntaxException {
    try {
      parser.parse(file);
    } catch (RecognitionException ex) {
      try {
        parserDebug.parse(file);
      } catch (RecognitionException ex2) {
        ExtendedStackTraceStream.print(extendedStackTrace, System.err);
        throw ex2;
      }

      throw new IllegalStateException(ex);
    }
  }

  protected static void addParametersForPath(Collection<Object[]> parameters, String path) throws URISyntaxException {
    Collection<File> files;
    files = listFiles(path, true);
    for (File file : files) {
      parameters.add(new Object[] { file });
    }
  }

  protected static Collection<Object[]> getParameters(String folder) throws URISyntaxException {
    return filesToParameters(listFiles("/" + folder + "/", true));
  }

  private static Collection<Object[]> filesToParameters(Collection<File> files) {
    Collection<Object[]> parameters = new ArrayList<Object[]>();
    for (File file : files) {
      parameters.add(new Object[] { file });
    }
    return parameters;
  }

  private static Collection<File> listFiles(String path, boolean recursive) throws URISyntaxException {
    return FileUtils.listFiles(new File(MiniCOwnExamplesTest.class.getResource(path).toURI()), null, recursive);
  }

}
