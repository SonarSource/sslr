/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.impl.ast;

import com.google.common.base.Throwables;
import com.sonar.sslr.api.AstNode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public final class AstXmlPrinter {

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
      throw Throwables.propagate(e);
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
    if (node.getTokenValue() != null) {
      writer.append(" tokenValue=\"" + node.getTokenValue() + "\"");
    }
    if (node.hasToken()) {
      writer.append(" tokenLine=\"" + node.getTokenLine() + "\" tokenColumn=\"" + node.getToken().getColumn() + "\"");
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
