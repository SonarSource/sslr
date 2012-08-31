/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit;

import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.toolkit.internal.SourceCodeModel;
import com.sonar.sslr.toolkit.internal.ToolkitPresenter;
import com.sonar.sslr.toolkit.internal.ToolkitViewImpl;
import org.sonar.colorizer.Tokenizer;

import javax.swing.SwingUtilities;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Toolkit {

  private final Parser<?> parser;
  private final List<Tokenizer> tokenizers;
  private final String title;

  public Toolkit(Parser<?> parser, List<Tokenizer> tokenizers, String title) {
    checkNotNull(parser);
    checkNotNull(tokenizers);
    checkNotNull(title);

    this.parser = parser;
    this.tokenizers = tokenizers;
    this.title = title;
  }

  public void run() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        SourceCodeModel model = new SourceCodeModel(parser, tokenizers);
        ToolkitPresenter presenter = new ToolkitPresenter(model);
        presenter.setView(new ToolkitViewImpl(presenter));
        presenter.run(title);
      }
    });
  }

}
