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
package org.sonar.sslr.toolkit;

import com.sonar.sslr.impl.Parser;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.internal.toolkit.SourceCodeModel;
import org.sonar.sslr.internal.toolkit.ToolkitPresenter;
import org.sonar.sslr.internal.toolkit.ToolkitViewImpl;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
  public Toolkit(final Parser parser, final List<Tokenizer> tokenizers, String title) {
    this(title, new AbstractConfigurationModel() {

      @Override
      public List<ConfigurationProperty> getProperties() {
        return Collections.emptyList();
      }

      @Override
      public List<Tokenizer> doGetTokenizers() {
        return tokenizers;
      }

      @Override
      public Parser doGetParser() {
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
    Objects.requireNonNull(title);

    this.title = title;
    this.configurationModel = configurationModel;
  }

  public void run() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
              UIManager.setLookAndFeel(info.getClassName());
              break;
            }
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }

        SourceCodeModel model = new SourceCodeModel(configurationModel);
        ToolkitPresenter presenter = new ToolkitPresenter(configurationModel, model);
        presenter.setView(new ToolkitViewImpl(presenter));
        presenter.run(title);
      }
    });
  }

}
