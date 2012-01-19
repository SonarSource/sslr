/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

public abstract class CommentAnalyser {

  /**
   * Check whether or not a comment line is blank
   * 
   * @param line
   *          A line of the comment, excluding the comment tags
   * @return true if the line is considered blank and false otherwise
   */
  public abstract boolean isBlank(String line);

  /**
   * Extract the content of a comment, i.e. remove the comment tags
   * 
   * @param comment
   *          Raw comment value
   * @return The content of the comment, without the tags
   */
  public abstract String getContents(String comment);

}
