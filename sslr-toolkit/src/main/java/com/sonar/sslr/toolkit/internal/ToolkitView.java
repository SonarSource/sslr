/*
 * Copyright (C) 2009-2012 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.toolkit.internal;

import com.sonar.sslr.api.AstNode;

import javax.annotation.Nullable;

import java.awt.Point;
import java.io.File;

public interface ToolkitView {

  /**
   * Launch the application.
   */
  void run();

  /**
   * Set the title of the application.
   *
   * @param title
   */
  void setTitle(String title);

  /**
   * Prompt the user for a file to parse and return it.

   * @return The file to parse, or null if no file was picked
   */
  @Nullable
  File pickFileToParse();

  /**
   * Display the given HTML highlighted source code in the source code editor.
   * Scrollbars state is undefined after a call to this method.
   *
   * @param htmlHighlightedSourceCode The HTML highlighted source code
   */
  void displayHighlightedSourceCode(String htmlHighlightedSourceCode);

  /**
   * Display the Abstract Syntax Tree starting on a given node
   *
   * @param astNode The root AST node, which tree should be displayed. Null if no abstract syntax tree must be shown
   */
  void displayAst(@Nullable AstNode astNode);

  /**
   * Display the given XML string.
   *
   * @param xml The XML representation of the source code's Abstract Syntax Tree
   */
  void displayXml(String xml);

  /**
   * Get the current scrollbars' position point
   *
   * @return The point
   */
  Point getScrollbarPosition();

  /**
   * Scroll the source code editor in order to make the given point visible.
   *
   * @param point
   */
  void scrollTo(Point point);

  /**
   * Get the source code currently entered in the source code editor.
   *
   * @return The source code
   */
  String getSourceCode();

}
