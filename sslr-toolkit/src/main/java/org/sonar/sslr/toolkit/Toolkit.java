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
import org.sonar.sslr.internal.toolkit.SourceCodeModel;
import org.sonar.sslr.internal.toolkit.ToolkitPresenter;
import org.sonar.sslr.internal.toolkit.ToolkitViewImpl;

import javax.swing.SwingUtilities;

public class Toolkit {

  private final String title;
  private final ConfigurationModel configurationModel;

  public Toolkit(String title, ConfigurationModel configurationModel) {
    Preconditions.checkNotNull(title);

    this.title = title;
    this.configurationModel = configurationModel;
  }

  public void run() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        SourceCodeModel model = new SourceCodeModel(configurationModel);
        ToolkitPresenter presenter = new ToolkitPresenter(configurationModel, model);
        presenter.setView(new ToolkitViewImpl(presenter));
        presenter.run(title);
      }
    });
  }

}
