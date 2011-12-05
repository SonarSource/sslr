/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.test.language.integration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import com.sonar.sslr.impl.events.ExtendedStackTraceStream;
import com.sonar.sslr.test.language.MiniLanguageGrammar;
import com.sonar.sslr.test.language.MiniLanguageParser;

@RunWith(value = Parameterized.class)
public class MiniLanguageOwnExamplesTest {

  private static ExtendedStackTrace extendedStackTrace = new ExtendedStackTrace();

  private File file = null;
  private static final Parser<MiniLanguageGrammar> parser = MiniLanguageParser.create();
  private static final Parser<MiniLanguageGrammar> parserDebug = MiniLanguageParser.create(extendedStackTrace);

  @Parameterized.Parameters
  public static Collection<Object[]> getFiles() throws URISyntaxException {
    return getParameters("OwnExamples");
  }

  public MiniLanguageOwnExamplesTest(File f) {
    this.file = f;
  }

  @Test
  public void parseSqlPlusSource() throws IOException, URISyntaxException {
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
    return FileUtils.listFiles(new File(MiniLanguageOwnExamplesTest.class.getResource(path).toURI()), null, recursive);
  }

}