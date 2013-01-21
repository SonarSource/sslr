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

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.internal.toolkit.SourceCodeModel;
import org.sonar.sslr.internal.toolkit.ToolkitPresenter;
import org.sonar.sslr.internal.toolkit.ToolkitViewImpl;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.util.Collections;
import java.util.List;

public class Toolkit {

  private final String title;
  private final ConfigurationModel configurationModel;

  /**
   * Create a Toolkit with a title, a static parser and static tokenizers.
   *
   * @param parser
   * @param tokenizers
   * @param title
   *
   * @deprecated in 1.17, use {@link #Toolkit(String, ConfigurationModel)} instead.
   */
  @Deprecated
  public Toolkit(final Parser<?> parser, final List<Tokenizer> tokenizers, String title) {
    this(title, new AbstractConfigurationModel() {

      public List<ConfigurationProperty> getProperties() {
        return Collections.EMPTY_LIST;
      }

      @Override
      public List<Tokenizer> doGetTokenizers() {
        return tokenizers;
      }

      @Override
      public Parser<? extends Grammar> doGetParser() {
        return parser;
      }

    });
  }

  /**
   * Creates a Toolkit with a title, and the given {@link ConfigurationModel}.
   *
   * @param title
   * @param configurationModel
   *
   * @since 1.17
   */
  public Toolkit(String title, ConfigurationModel configurationModel) {
    Preconditions.checkNotNull(title);

    this.title = title;
    this.configurationModel = configurationModel;
  }

  public void run() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
          Throwables.propagate(e);
        }

        SourceCodeModel model = new SourceCodeModel(configurationModel);
        ToolkitPresenter presenter = new ToolkitPresenter(configurationModel, model);
        presenter.setView(new ToolkitViewImpl(presenter));
        presenter.run(title);
      }
    });
  }

}
