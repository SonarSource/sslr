/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.ast;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class AstXmlPrinter {

  private final AstNode rootNode;
  private final Writer writer;

  private AstXmlPrinter(AstNode rootNode, Writer writer) {
    this.rootNode = rootNode;
    this.writer = writer;
  }

  public static String print(AstNode rootNode) {
    StringWriter writer = new StringWriter();
    print(rootNode, writer);
    return writer.toString();
  }

  public static void print(AstNode rootNode, Writer writer) {
    AstXmlPrinter printer = new AstXmlPrinter(rootNode, writer);
    printer.print();
  }

  private void print() {
    try {
      printNode(0, rootNode);
    } catch (IOException e) {
      throw new RuntimeException("A problem occured when generating an XML stream from the AST.", e);
    }
  }

  private void printNode(int level, AstNode node) throws IOException {
    if (level != 0) {
      writer.append("\n");
    }
    appendSpaces(level);
    if (node.hasChildren()) {
      writer.append("<");
      appendNodecontent(node);
      writer.append(">");
      toXmlChildren(level, node);
      appendCarriageReturnAndSpaces(level);
      writer.append("</").append(node.getName()).append(">");
    } else {
      writer.append("<");
      appendNodecontent(node);
      writer.append("/>");
    }
  }

  private void appendNodecontent(AstNode node) throws IOException {
    writer.append(node.getName());
    if ( !node.isARule() && node.getTokenValue() != null && !node.getName().equals(node.getTokenValue())) {
      writer.append(" value=\"" + node.getTokenValue() + "\"");
    }
    if (node.hasToken()) {
      writer.append(" line=\"" + node.getTokenLine() + "\" col=\"" + node.getToken().getColumn() + "\"");
    }
  }

  private void toXmlChildren(int level, AstNode node) throws IOException {
    for (AstNode child : node.getChildren()) {
      printNode(level + 1, child);
    }
  }

  private void appendCarriageReturnAndSpaces(int level) throws IOException {
    writer.append("\n");
    appendSpaces(level);
  }

  private void appendSpaces(int level) throws IOException {
    for (int i = 0; i < level; i++) {
      writer.append("  ");
    }
  }
}
