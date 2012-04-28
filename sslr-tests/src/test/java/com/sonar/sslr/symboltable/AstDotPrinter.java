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
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class AstDotPrinter {

  private final AstNode rootNode;
  private final Writer writer;

  private int num;

  private AstDotPrinter(AstNode rootNode, Writer writer) {
    this.rootNode = rootNode;
    this.writer = writer;
  }

  public static String print(AstNode rootNode) {
    StringWriter writer = new StringWriter();
    print(rootNode, writer);
    return writer.toString();
  }

  public static void print(AstNode rootNode, Writer writer) {
    new AstDotPrinter(rootNode, writer).print();
  }

  private void print() {
    try {
      writer.append("digraph {\n");
      writer.append("ordering=out;\n");
      writer.append("ranksep=.3;\n");
      writer.append("node [shape=box, fixedsize=false, fontsize=11, fontname=\"Helvetica-bold\", width=.25, height=.25];\n");
      writer.append("edge [arrowsize=.5, color=\"black\"]\n");

      num = 0;
      printNode1(rootNode);

      num = 0;
      printNode2(rootNode);

      writer.append("}\n");
    } catch (IOException e) {
      throw new RuntimeException("A problem occured when generating a DOT stream from the AST.", e);
    }
  }

  private void printNode1(AstNode node) throws IOException {
    writer.append("n" + num + " [label=\"" + node.getName() + "\"]\n");
    for (AstNode child : node.getChildren()) {
      num++;
      printNode1(child);
    }
  }

  private void printNode2(AstNode node) throws IOException {
    int cur = num;
    for (AstNode child : node.getChildren()) {
      num++;
      writer.append("n" + cur + " -> n" + num + "\n");
      printNode2(child);
    }
  }
}
