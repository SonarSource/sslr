/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.io.File;

public abstract class PreprocessingDirective {

  private final File definitionFile;

  private final int definitionLine;

  public PreprocessingDirective(File definitionFile, int definitionLine) {
    this.definitionFile = definitionFile;
    this.definitionLine = definitionLine;
  }

  public File getDefinitionFile() {
    return definitionFile;
  }

  public int getDefinitionLine() {
    return definitionLine;
  }

}
