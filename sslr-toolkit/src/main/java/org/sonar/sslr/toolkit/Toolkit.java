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
package org.sonar.sslr.toolkit;

import org.sonar.sslr.internal.toolkit.SourceCodeModel;
import org.sonar.sslr.internal.toolkit.ToolkitPresenter;
import org.sonar.sslr.internal.toolkit.ToolkitViewImpl;

import com.sonar.sslr.impl.Parser;
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
