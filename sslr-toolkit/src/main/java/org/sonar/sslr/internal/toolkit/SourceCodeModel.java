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
package org.sonar.sslr.internal.toolkit;

import org.sonar.sslr.toolkit.ConfigurationModel;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

public class SourceCodeModel {

  private final ConfigurationModel configurationModel;
  private final HtmlRenderer htmlRenderer = new HtmlRenderer(new HtmlOptions(false, null, false));

  private String sourceCode;
  private AstNode astNode;

  public SourceCodeModel(ConfigurationModel configurationModel) {
    Preconditions.checkNotNull(configurationModel);

    this.configurationModel = configurationModel;
  }

  public void setSourceCode(File source, Charset charset) {
    this.astNode = configurationModel.getParser().parse(source);

    try {
      this.sourceCode = Files.toString(source, charset);
    } catch (IOException e) {
      Throwables.propagate(e);
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
