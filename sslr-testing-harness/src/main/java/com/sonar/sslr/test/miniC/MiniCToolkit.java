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
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.toolkit.internal.SourceCodeModel;
import com.sonar.sslr.toolkit.internal.ToolkitPresenter;
import com.sonar.sslr.toolkit.internal.ToolkitViewImpl;

public final class MiniCToolkit {

  private MiniCToolkit() {
  }

  public static void main(String[] args) {
    // System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MiniC Toolkit");
    // SsdkGui ssdkGui = new SsdkGui(MiniCParser.create(), MiniCColorizer.getTokenizers());
    // ssdkGui.setVisible(true);
    // ssdkGui.setSize(1000, 800);
    // ssdkGui.setTitle("MiniC : Toolkit");

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        SourceCodeModel model = new SourceCodeModel(MiniCParser.create(), MiniCColorizer.getTokenizers());
        ToolkitPresenter presenter = new ToolkitPresenter(model);
        presenter.setView(new ToolkitViewImpl(presenter));
        presenter.run("MiniC : Toolkit");
      }
    });
  }

}
