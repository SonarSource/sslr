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
package org.sonar.sslr.internal.toolkit;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;
import org.sonar.colorizer.Tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SourceCodeModel {

  private final Parser<?> parser;
  private final List<Tokenizer> tokenizers;
  private final HtmlRenderer htmlRenderer = new HtmlRenderer(new HtmlOptions(false, null, false));

  private String sourceCode;
  private AstNode astNode;

  public SourceCodeModel(Parser<?> parser, List<Tokenizer> tokenizers) {
    checkNotNull(parser);
    checkNotNull(tokenizers);

    this.parser = parser;
    this.tokenizers = tokenizers;
  }

  public void setSourceCode(File source, Charset charset) {
    this.astNode = parser.parse(source);

    try {
      this.sourceCode = Files.toString(source, charset);
    } catch (IOException e) {
      Throwables.propagate(e);
    }
  }

  public void setSourceCode(String sourceCode) {
    this.astNode = parser.parse(sourceCode);
    this.sourceCode = sourceCode;
  }

  public String getHighlightedSourceCode() {
    return htmlRenderer.render(new StringReader(sourceCode), tokenizers);
  }

  public String getXml() {
    return AstXmlPrinter.print(astNode);
  }

  public AstNode getAstNode() {
    return astNode;
  }

}
