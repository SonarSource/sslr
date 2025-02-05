/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
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
package org.sonar.sslr.internal.toolkit;

import org.sonar.sslr.toolkit.ConfigurationModel;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class SourceCodeModel {

  private final ConfigurationModel configurationModel;
  private final HtmlRenderer htmlRenderer = new HtmlRenderer(new HtmlOptions(false, null, false));

  private String sourceCode;
  private AstNode astNode;

  public SourceCodeModel(ConfigurationModel configurationModel) {
    Objects.requireNonNull(configurationModel);

    this.configurationModel = configurationModel;
  }

  public void setSourceCode(File source, Charset charset) {
    this.astNode = configurationModel.getParser().parse(source);

    try {
      this.sourceCode = new String(Files.readAllBytes(Paths.get(source.getPath())), charset);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void setSourceCode(String sourceCode) {
    this.astNode = configurationModel.getParser().parse(sourceCode);
    this.sourceCode = sourceCode;
  }

  public String getHighlightedSourceCode() {
    return htmlRenderer.render(new StringReader(sourceCode), configurationModel.getTokenizers());
  }

  public String getXml() {
    return AstXmlPrinter.print(astNode);
  }

  public AstNode getAstNode() {
    return astNode;
  }

}
