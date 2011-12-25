/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sdk;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;

import org.apache.commons.io.IOUtils;
import org.sonar.colorizer.HtmlOptions;
import org.sonar.colorizer.HtmlRenderer;
import org.sonar.colorizer.Tokenizer;

@SuppressWarnings("serial")
public class SourceCodeViewer extends JEditorPane {

  private static Logger log = Logger.getLogger("DevSuiteLogger");
  private final List<Tokenizer> colorizerChannels;
  private static final String CSS_PATH = "/sourceCodeViewer.css";

  public SourceCodeViewer(List<Tokenizer> colorizerChannels) {
    setEditable(false);
    this.colorizerChannels = colorizerChannels;
  }

  public void loadAndColorizeFile(File file) {
    try {
      setContentType("text/html");
      HtmlRenderer htmlRendere = new HtmlRenderer(new HtmlOptions(false, null, false));
      String colorizedSourceCode = addHtmlHeader(htmlRendere.render(new FileReader(file), colorizerChannels));
      setText(colorizedSourceCode);
    } catch (IOException e1) {
      log.log(Level.SEVERE, "Unable to read source code file : " + file.getAbsolutePath());
    }
  }

  private String addHtmlHeader(String htmlBody) {
    StringBuffer html = new StringBuffer();
    html.append("<html><head><style type=\"text/css\">");
    html.append(getCss());
    html.append("</style></head><body><pre class=\"code\">");
    html.append(htmlBody);
    html.append("</pre></body></html>");
    return html.toString();
  }

  private Object getCss() {
    try {
      return IOUtils.toString(SourceCodeViewer.class.getResourceAsStream(CSS_PATH));
    } catch (Exception e) {
      throw new RuntimeException("Unable to find '" + CSS_PATH + "' file", e);
    }
  }
}
