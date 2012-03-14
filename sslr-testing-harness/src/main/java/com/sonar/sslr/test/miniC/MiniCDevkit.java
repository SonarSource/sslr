/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.devkit.SsdkGui;

public final class MiniCDevkit {

  private MiniCDevkit() {
  }

  public static void main(String[] args) {
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "SSDK");
    SsdkGui ssdkGui = new SsdkGui(MiniCParser.create(), MiniCColorizer.getTokenizers());
    ssdkGui.setVisible(true);
    ssdkGui.setSize(1000, 800);
    ssdkGui.setTitle("MiniC : SonarSource Development Kit");
  }

}
