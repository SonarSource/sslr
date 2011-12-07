/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.miniC;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;

public final class CheckUtils {

  private static final Parser<MiniCGrammar> p = MiniCParser.create();
  private static final MiniCGrammar g = p.getGrammar();

  public static AstNode parse(String filePath) {
    File file = FileUtils.toFile(CheckUtils.class.getResource(filePath));
    if (file == null || !file.exists()) {
      throw new AssertionError("The file \"" + filePath + "\" does not exist.");
    }

    return p.parse(file);
  }

  public static MiniCGrammar getGrammar() {
    return g;
  }

}
