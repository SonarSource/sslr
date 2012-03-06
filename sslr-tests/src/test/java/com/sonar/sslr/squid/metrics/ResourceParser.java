/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.metrics;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.indexer.QueryByType;

import com.sonar.sslr.squid.AstScanner;
import com.sonar.sslr.squid.SquidAstVisitor;
import com.sonar.sslr.test.miniC.MiniCAstScanner;
import com.sonar.sslr.test.miniC.MiniCGrammar;

public class ResourceParser {

  public static SourceFile scanFile(String filePath, SquidAstVisitor<MiniCGrammar>... visitors) {
    return scanFile(filePath, false, visitors);
  }

  public static SourceFile scanFileIgnoreHeaderComments(String filePath, SquidAstVisitor<MiniCGrammar>... visitors) {
    return scanFile(filePath, true, visitors);
  }

  private static SourceFile scanFile(String filePath, boolean ignoreHeaderComments, SquidAstVisitor<MiniCGrammar>... visitors) {
    AstScanner<MiniCGrammar> scanner = ignoreHeaderComments ? MiniCAstScanner.createIgnoreHeaderComments(visitors) : MiniCAstScanner
        .create(visitors);
    File file = FileUtils.toFile(ResourceParser.class.getResource(filePath));
    if (file == null || !file.exists()) {
      throw new IllegalArgumentException("The file located under \"" + filePath + "\" was not found.");
    }
    scanner.scanFile(file);
    assertThat(scanner.getIndex().search(new QueryByType(SourceFile.class)).size(), is(1));
    return (SourceFile) scanner.getIndex().search(new QueryByType(SourceFile.class)).iterator().next();
  }

}
