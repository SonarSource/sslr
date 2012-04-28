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
