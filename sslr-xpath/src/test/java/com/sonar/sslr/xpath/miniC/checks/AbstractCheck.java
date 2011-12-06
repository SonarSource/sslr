/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.miniC.checks;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;

public class AbstractCheck {

  public final Parser<MiniCGrammar> p = MiniCParser.create();
  public final MiniCGrammar g = p.getGrammar();

  public final AstNode parse(String filePath) {
    File file = FileUtils.toFile(AbstractCheck.class.getResource(filePath));
    if (file == null || !file.exists()) {
      throw new AssertionError("The file \"" + filePath + "\" does not exist.");
    }

    return p.parse(file);
  }

}
