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
import java.util.List;

/**
 * Contract interface for the view.
 *
 * Note that *none* of the methods here-under should generate *any* event back to the presenter.
 * Only end-user interactions are supposed to generate events.
 */
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
   * Display the abstract syntax tree view starting from a given node.
   *
   * @param astNode The root AST node or null if no abstract syntax tree must be shown
   */
  void displayAst(@Nullable AstNode astNode);

  /**
   * Display the given string in the XML view.
   *
   * @param xml The string to display
   */
  void displayXml(String xml);

  /**
   * Get the current source code editor scrollbars' position point.
   *
   * @return The point
   */
  Point getSourceCodeScrollbarPosition();

  /**
   * Scroll the source code editor in order to make the given point visible.
   *
   * @param point to make visible
   */
  void scrollSourceCodeTo(Point point);

  /**
   * Get the source code currently entered in the source code editor.
   *
   * @return The source code
   */
  String getSourceCode();

  /**
   * Get the text currently entered in the XPath field.
   *
   * @return The XPath field text
   */
  String getXPath();

  /**
   * Select the given AST node in the abstract syntax tree view.
   *
   * @param astNode The AST node to select, null will lead to a no operation
   */
  void selectAstNode(@Nullable AstNode astNode);

  /**
   * Clear all the selections in the abstract syntax tree view.
   */
  void clearAstSelections();

  /**
   * Scroll the abstract syntax tree view in order to make the given AST node visible.
   *
   * @param astNode The AST node to make visible, null will lead to a no operation
   */
  void scrollAstTo(@Nullable AstNode astNode);

  /**
   * Highlight the given AST node in the source code editor.
   *
   * @param astNode The AST node to highlight
   */
  void highlightSourceCode(AstNode astNode);

  /**
   * Clear all the highlights in the source code editor.
   */
  void clearSourceCodeHighlights();

  /**
   * Scroll the source code editor in order to make the given AST node visible.
   *
   * @param astNode The AST node to make visible, null will lead to a no operation
   */
  void scrollSourceCodeTo(@Nullable AstNode astNode);

  /**
   * Disable the XPath evaluate button.
   */
  void disableXPathEvaluateButton();

  /**
   * Enable the XPath evaluate button.
   */
  void enableXPathEvaluateButton();

  /**
   * Get the AST node which follows the current source code editor text cursor position.
   *
   * @return The following AST node, or null if there is no such node
   */
  @Nullable
  AstNode getAstNodeFollowingCurrentSourceCodeTextCursorPosition();

  /**
   * Get the list of nodes currently selected in the abstract syntax tree view.
   *
   * @return The list of selected AST nodes
   */
  List<AstNode> getSelectedAstNodes();

  /**
   * Append the given message to the console view.
   *
   * @param message The message to append
   */
  public void appendToConsole(String message);

  /**
   * Set the focus on the console view.
   */
  public void setFocusOnConsoleView();

}
