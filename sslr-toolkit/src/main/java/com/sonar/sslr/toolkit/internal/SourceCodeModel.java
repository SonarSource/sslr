/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit.internal;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.guava.internal.Files;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ast.AstXmlPrinter;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;
import org.sonar.colorizer.Tokenizer;

import java.io.File;
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
    astNode = parser.parse(source);
    sourceCode = Files.toString(source, charset);
  }

  public void setSourceCode(String source) {
    astNode = parser.parse(source);
    sourceCode = source;
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
