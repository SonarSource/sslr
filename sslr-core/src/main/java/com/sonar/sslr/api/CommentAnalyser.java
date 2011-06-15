/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

public abstract class CommentAnalyser {

  /**
   * 
   * @param commenValue
   *          value of the comment
   * @return true if the comment value is not considered as a blank comment
   */
  public abstract boolean isBlank(String commenValue);
}
