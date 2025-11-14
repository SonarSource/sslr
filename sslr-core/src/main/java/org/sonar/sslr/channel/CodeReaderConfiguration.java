/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration parameters used by a CodeReader to handle some specificities.
 */
public class CodeReaderConfiguration {

  public static final int DEFAULT_TAB_WIDTH = 1;

  private int tabWidth = DEFAULT_TAB_WIDTH;

  private List<CodeReaderFilter<?>> codeReaderFilters = new ArrayList<>();

  /**
   * @return the tabWidth
   */
  public int getTabWidth() {
    return tabWidth;
  }

  /**
   * @param tabWidth
   *          the tabWidth to set
   */
  public void setTabWidth(int tabWidth) {
    this.tabWidth = tabWidth;
  }

  /**
   * @return the codeReaderFilters
   */
  @SuppressWarnings("rawtypes")
  public CodeReaderFilter[] getCodeReaderFilters() {
    return codeReaderFilters.toArray(new CodeReaderFilter[codeReaderFilters.size()]);
  }

  /**
   * @param codeReaderFilters
   *          the codeReaderFilters to set
   */
  public void setCodeReaderFilters(CodeReaderFilter<?>... codeReaderFilters) {
    this.codeReaderFilters = new ArrayList<>(Arrays.asList(codeReaderFilters));
  }

  /**
   * Adds a code reader filter
   *
   * @param codeReaderFilter
   *          the codeReaderFilter to add
   */
  public void addCodeReaderFilters(CodeReaderFilter<?> codeReaderFilter) {
    this.codeReaderFilters.add(codeReaderFilter);
  }

  public CodeReaderConfiguration cloneWithoutCodeReaderFilters() {
    CodeReaderConfiguration clone = new CodeReaderConfiguration();
    clone.setTabWidth(tabWidth);
    return clone;
  }

}
